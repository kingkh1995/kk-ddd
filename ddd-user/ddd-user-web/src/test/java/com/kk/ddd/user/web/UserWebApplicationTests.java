package com.kk.ddd.user.web;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.bean.NettyDelayer;
import com.kk.ddd.support.constant.AccountTypeEnum;
import com.kk.ddd.support.constant.UserStateEnum;
import com.kk.ddd.support.distributed.DistributedLockFactory;
import com.kk.ddd.support.model.dto.AccountDTO;
import com.kk.ddd.support.type.LongId;
import com.kk.ddd.support.type.MillionYuan;
import com.kk.ddd.support.type.PageSize;
import com.kk.ddd.user.application.assembler.AccountDTOAssembler;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.AccountType;
import com.kk.ddd.user.domain.type.Hash;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.domain.type.UserState;
import com.kk.ddd.user.domain.type.Username;
import com.kk.ddd.user.persistence.AccountMapper;
import com.kk.ddd.user.persistence.AccountPO;
import com.kk.ddd.user.persistence.UserMapper;
import com.kk.ddd.user.repository.AccountRepository;
import com.kk.ddd.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class UserWebApplicationTests {

  @Autowired private StringRedisTemplate stringRedisTemplate;

  @Autowired private CacheManager cacheManager;

  @Autowired private NettyDelayer delayer;

  @Autowired private DistributedLockFactory distributedLockFactory;

  @Autowired private RedissonClient redissonClient;

  @Autowired private CuratorFramework curatorClient;

  @Autowired private UserMapper userMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private AccountMapper accountMapper;

  @Autowired private AccountRepository accountRepository;

  @Autowired private AccountDTOAssembler accountDTOAssembler;

  @Test
  @Transactional
  void testCache() {
    var cache = cacheManager.getCache("kk:test");
    var key = "testKey";
    cache.clear();
    System.out.println(cache.get(key));
    System.out.println(cache.get(key, () -> accountMapper.selectById(1L).get()));
    System.out.println(cache.get(key, AccountPO.class));
    cache.evict(key);
    System.out.println(cache.get(key, AccountPO.class));
    cache.put(key, null);
    System.out.println(cache.get(key, () -> accountMapper.selectById(4L).get()));
    System.out.println(cache.putIfAbsent(key, null));
    try {
      // always throw exception with transaction
      System.out.println(cache.get(key, String.class));
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    cache.clear();
  }

  @Test
  @Transactional
  void testRepository() throws Exception {
    var all = userMapper.selectAll();
    var byUsername = userRepository.find(Username.of(all.get(0).getName())).get();
    System.out.println(Jackson.object2String(byUsername));
    userRepository.find(byUsername.getId());
    byUsername.getAccounts().get(3).unbind();
    userRepository.save(byUsername);
    var userIdSet = Set.of(UserId.of(1L), UserId.of(2L), UserId.of(3L));
    var map = userRepository.find(userIdSet);
    System.out.println(Jackson.object2String(map));
    userRepository.remove(byUsername);
    System.out.println(Jackson.object2String(userRepository.find(byUsername.getId())));
    System.out.println(Jackson.object2String(userRepository.find(userIdSet)));
    // 等待延时删除完成
    Thread.sleep(2000);
  }

  @Test
  void testDelayer() throws Exception {
    var downLatch = new CountDownLatch(2);
    delayer.delay(
        () -> {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          log.info("3");
          downLatch.countDown();
        },
        2,
        TimeUnit.SECONDS);
    delayer.delay(
        () -> {
          log.info("2");
          downLatch.countDown();
        },
        2,
        TimeUnit.SECONDS);
    log.info("1");
    downLatch.await();
    log.info("finish！");
  }

  @Test
  void testZookeeper() throws Exception {
    System.out.println(curatorClient.getNamespace());
    String path = "/kk/test";
    // 永久型事件监听，Watcher每次触发后都会被移除。
    var cacheBridge = CuratorCache.bridgeBuilder(curatorClient, path).build();
    // 添加监听器
    cacheBridge
        .listenable()
        .addListener(
            // 监听所有事件
            CuratorCacheListener.builder()
                .forAll(
                    (type, oldData, data) ->
                        log.info("type:{}, oldData:{}, data:{}", type, oldData, data))
                .build());
    cacheBridge.start();
    // 删除节点
    curatorClient
        .delete()
        .quietly() // 节点不存在时不会抛出异常
        .deletingChildrenIfNeeded() // 删除所有子节点
        .forPath(path);
    // 创建节点并返回节点路径（路径可能被修改，如开启保护机制）
    var pPath =
        curatorClient
            .create()
            .creatingParentContainersIfNeeded() // 沿路径递归创建所需父容器节点
            .withMode(CreateMode.PERSISTENT) // 需要为永久节点，临时节点无法创建子节点
            .forPath(path, "这是永久父节点！".getBytes());
    System.out.println(pPath);
    var cPath =
        curatorClient
            .create()
            .withProtection() // Curator保护机制，防止服务器创建成功未通知到客户端，创建节点时自动添加GUID
            .withMode(CreateMode.EPHEMERAL)
            .forPath(path + "/child", "这是临时子节点！".getBytes());
    System.out.println(cPath);
    // 为Client添加一个异步事件监听器（只监听无回调函数的事件，能获取到inBackground(Object context)传递的参数）
    curatorClient
        .getCuratorListenable()
        .addListener((client, event) -> log.info("CuratorListener -> '{}'", event));
    Thread.sleep(2000);
    // 异步新增
    var node = path + "/" + UUID.randomUUID() + "/1";
    var forPath =
        curatorClient
            .create()
            .creatingParentContainersIfNeeded()
            .withMode(CreateMode.PERSISTENT)
            .inBackground("first") // 异步执行并传递上下文，事件完成后监听器会接受到上下文对象
            .forPath(node, "这是异步创建的子节点！".getBytes());
    curatorClient
        .create()
        .creatingParentContainersIfNeeded()
        .withMode(CreateMode.PERSISTENT)
        .inBackground("second")
        .forPath(node + "/2");
    Thread.sleep(2000);
    // 查询子节点
    curatorClient.getChildren().forPath(path);
    // 查询数据和节点信息
    var stat = new Stat();
    System.out.println(new String(curatorClient.getData().storingStatIn(stat).forPath(pPath)));
    System.out.println(stat);
    System.out.println(new String(curatorClient.getData().storingStatIn(stat).forPath(cPath)));
    System.out.println(stat);
    System.out.println(
        new String(
            curatorClient
                .getData()
                .storingStatIn(stat)
                // 添加临时监听器，只能监听一次节点的事件。
                .usingWatcher((CuratorWatcher) event -> log.info("CuratorWatcher : {}", event))
                .forPath(node)));
    System.out.println(stat);
    // 异步删除
    System.out.println("异步删除节点,此时节点创建状态，node:" + forPath);
    curatorClient
        .delete()
        .guaranteed() // 保证删除，会一直重试直到连接失效。
        // 异步执行，完成后执行callback
        .inBackground((client, event) -> log.info("删除节点完成 -> '{}'", event))
        .forPath(node);
    curatorClient.delete().inBackground().forPath(node + "/2");
    Thread.sleep(5000);
  }

  @Test
  void testFlashSale() {
    var tag = "{kk:test:tag}";
    var kl = tag + "limit";
    var ku = tag + "user";
    var kc = tag + "context";
    var id = tag + "testId";
    var script = new DefaultRedisScript<Long>();
    script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/flash_sale.lua")));
    script.setResultType(Long.class);
    var sha1 = script.getSha1();
    System.out.println(sha1);
    System.out.println(stringRedisTemplate.execute(script, List.of(tag, id), "2", "num:2"));
    stringRedisTemplate.opsForValue().set(tag, "20");
    stringRedisTemplate.opsForValue().set(kl, "3");
    System.out.println(stringRedisTemplate.execute(script, List.of(tag, id), "5", "num:2"));
    System.out.println("stock:" + stringRedisTemplate.opsForValue().get(tag));
    // 键值均使用字符串序列化
    var rScript = redissonClient.getScript(StringCodec.INSTANCE);
    var eval1 =
        rScript.evalSha(Mode.READ_WRITE, sha1, ReturnType.INTEGER, List.of(tag, id), "2", "num:2");
    System.out.println(eval1);
    System.out.println("stock:" + stringRedisTemplate.opsForValue().get(tag));
    var eval2 =
        rScript.evalSha(Mode.READ_WRITE, sha1, ReturnType.INTEGER, List.of(tag, id), "1", "num:1");
    System.out.println(eval2);
    System.out.println("stock:" + stringRedisTemplate.opsForValue().get(tag));
    var eval3 =
        rScript.evalSha(Mode.READ_WRITE, sha1, ReturnType.INTEGER, List.of(tag, id), "1", "num:1");
    System.out.println(eval3);
    System.out.println("stock:" + stringRedisTemplate.opsForValue().get(tag));
    stringRedisTemplate.delete(List.of(tag, kl, ku, kc));
    rScript.scriptFlush();
  }

  @Test
  void testLock() throws Exception {
    var name1 = distributedLockFactory.getLockNameGenerator().generate("kk", "1");
    var name2 = distributedLockFactory.getLockNameGenerator().generate("kk", "2");
    var name3 = distributedLockFactory.getLockNameGenerator().generate("kk", "3");
    var list = List.of(name1, name2, name3);
    var multiLock = distributedLockFactory.getMultiLock(list);
    log.info("multi lock '{}' return '{}'", list, multiLock.tryLock());
    var slist = List.of(name2, name3);
    var smultiLock = distributedLockFactory.getMultiLock(slist);
    log.info("multi lock '{}' return '{}'", slist, smultiLock.tryLock());
    var lock = distributedLockFactory.getLock(name1);
    log.info("lock '{}' return '{}'", name1, lock.tryLock());
    log.info("lock '{}' return '{}'", name1, lock.tryLock());
    lock.unlock();
    lock.unlock();
    CompletableFuture.runAsync(() -> log.info("async lock '{}' return '{}'", name1, lock.tryLock()))
        .get();
    log.info("lock '{}' return '{}'", list, multiLock.tryLock());
    multiLock.unlock();
    multiLock.unlock();
    smultiLock.unlock();
  }

  @Test
  void testMapstruct() {
    var account = accountRepository.find(AccountId.of(1L)).get();
    System.out.println(Jackson.object2String(account));
    var accountDTO = accountDTOAssembler.toDTO(account);
    System.out.println(accountDTO);
    System.out.println(accountDTOAssembler.fromDTO(accountDTO));
    System.out.println(accountDTOAssembler.fromDTO(List.of(accountDTO)));
    var target = new AccountDTO();
    accountDTOAssembler.buildDTO(UserId.of(100L), account, target);
    System.out.println(target);
  }

  @Test
  @Transactional
  void testMybatis() {
    var userPOList = userMapper.selectAll();
    System.out.println(Jackson.object2String(userPOList));
    var userPO = userMapper.selectById(userPOList.get(0).getId()).get();
    userPO.setPhone(null);
    userPO.setEmail(null);
    userMapper.updateById(userPO);
    System.out.println(Jackson.object2String(userMapper.selectById(userPO.getId())));
    var accountPOS = accountMapper.selectByUserId(userPO.getId());
    var accountPO = accountPOS.get(0);
    accountPO.setType(null);
    accountMapper.updateById(accountPO);
    System.out.println(Jackson.object2String(accountMapper.selectById(accountPO.getId())));
    var page =
        PageHelper.startPage(2, 1).doSelectPage(() -> accountMapper.selectByUserId(userPO.getId()));
    System.out.println(Jackson.object2String(page));
  }

  @Test
  void testJacksonWithType() {
    System.out.println(Jackson.object2String(PageSize.DEFAULT));
    LongId longId = LongId.of(123456789L);
    System.out.println(Jackson.object2String(longId));
    AccountId accountId = AccountId.of(123456789L);
    System.out.println(Jackson.object2String(accountId));
    System.out.println(Jackson.parse(Jackson.object2String(accountId), AccountId.class));
    var id1 = Jackson.parse("1234", new TypeReference<LongId>() {});
    var id2 = Jackson.parse("111", new TypeReference<AccountId>() {});
    System.out.println(id1.getValue());
    System.out.println(id2.getValue());
    System.out.println(
        Jackson.parse(Jackson.object2String("66666"), new TypeReference<AccountId>() {}));
    var accountState = UserState.of(UserStateEnum.INIT);
    var json = Jackson.object2String(accountState);
    System.out.println(Jackson.parse(json, new TypeReference<UserState>() {}).toEnum());
    var account =
        Account.builder()
            .id(AccountId.of(10))
            .type(AccountType.of(AccountTypeEnum.USERNAME))
            .build();
    var s = Jackson.object2String(account);
    System.out.println(s);
    System.out.println(Jackson.parse(s, Account.class));
    System.out.println(Jackson.parse(s, Account.class).getCreateTime());
    System.out.println(MillionYuan.of(new BigDecimal("110.6")).toFormattedString());
    var hashJson = Jackson.object2String(Hash.valueOf("MD5", "123456", 3, "Hash"));
    System.out.println(hashJson);
    System.out.println(Jackson.parse(hashJson));
  }

  @Test
  void testTTL() throws InterruptedException {
    var executor = Executors.newSingleThreadExecutor();
    var ttlExecutor = TtlExecutors.getTtlExecutor(executor);
    // 先提交一个任务，让线程池启动，创建好线程。
    executor.execute(() -> {});
    var countDownLatch = new CountDownLatch(2);
    var itl = new InheritableThreadLocal<>();
    var ttl = new TransmittableThreadLocal<>();
    final var o = new Object();
    itl.set(o);
    ttl.set(o);
    MDC.put("kk", "mdc");
    executor.execute(
        () -> {
          System.out.println(o == itl.get()); // false
          System.out.println(MDC.get("kk")); // null
          countDownLatch.countDown();
        });
    ttlExecutor.execute(
        () -> {
          System.out.println(o == itl.get()); // false
          System.out.println(MDC.get("kk")); // mdc
          countDownLatch.countDown();
        });
    countDownLatch.await();
    System.out.println(MDC.get("kk")); // mdc
    MDC.remove("kk");
    executor.execute(
        () -> {
          System.out.println((o == ttl.get())); // false
          System.out.println(MDC.get("kk")); // null
          countDownLatch.countDown();
        });
    ttlExecutor.execute(
        () -> {
          System.out.println((o == ttl.get())); // true
          System.out.println(MDC.get("kk")); // null
          countDownLatch.countDown();
        });
    countDownLatch.await();
    System.out.println(MDC.get("kk")); // null
  }
}
