package com.kkk.op.user.web;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.bean.WheelTimer;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.marker.Cache;
import com.kkk.op.support.marker.Cache.ValueWrapper;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.model.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.support.types.PageSize;
import com.kkk.op.support.types.StampedTime;
import com.kkk.op.support.types.TenThousandYuan;
import com.kkk.op.user.assembler.AccountAssembler;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountState;
import com.kkk.op.user.domain.types.UserId;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class OpUserWebApplicationTests {

  @Autowired private Kson kson;

  @Autowired private UserMapper userMapper;

  @Autowired private AccountMapper accountMapper;

  @Autowired private AccountRepository accountRepository;

  @Autowired private AccountAssembler accountAssembler;

  @Autowired private Cache cache;

  @Autowired private Validator validator;

  @Autowired private DistributedLock distributedLock;

  @Autowired private WheelTimer wheelTimer;

  @Test
  void testCache() {
    System.out.println(cache.getName());
    System.out.println(cache.get("Test", Account.class));
    System.out.println(cache.get("Test0", Account.class, () -> null));
    System.out.println(cache.get("Test0", Account.class));
    System.out.println(
        cache
            .get("Test", Account.class, () -> accountRepository.find(AccountId.from(1L)).get())
            .orElse(null));
    System.out.println(cache.get("Test", Account.class).map(ValueWrapper::get));
  }

  @Test
  void testMapstruct() {
    var account = accountRepository.find(AccountId.from(1L)).get();
    var accountDTO = accountAssembler.toDTO(account);
    System.out.println(accountDTO);
    System.out.println(accountAssembler.fromDTO(accountDTO));
    System.out.println(accountAssembler.fromDTO(List.of(accountDTO)));
    var target = new AccountDTO();
    accountAssembler.buildDTO(UserId.from(100L), account, target);
    System.out.println(target);
  }

  @Test
  void testMybatis() {
    var userDOList = userMapper.selectList();
    System.out.println(kson.writeJson(userDOList));
    var userDO = userMapper.selectById(userDOList.get(0).getId()).get();
    userDO.setGender(null);
    userDO.setAge(null);
    userDO.setEmail(null);
    userMapper.updateById(userDO);
    System.out.println(kson.writeJson(userMapper.selectById(userDO.getId())));
    var accountDOS = accountMapper.selectListByUserId(userDO.getId());
    var accountDO = accountDOS.get(0);
    accountDO.setState(null);
    accountMapper.updateById(accountDO);
    System.out.println(kson.writeJson(accountMapper.selectById(accountDO.getId())));
    var page =
        PageHelper.startPage(2, 1)
            .doSelectPage(() -> userMapper.selectListByGender(userDO.getGender()));
    System.out.println(kson.writeJson(page));
    System.out.println(accountMapper.deleteByUserId(userDOList.get(1).getId()));
  }

  @Test
  void testJacksonWithType() {
    System.out.println(kson.writeJson(PageSize.DEFAULT_SIZE));
    LongId longId = LongId.from(123456789L);
    System.out.println(kson.writeJson(longId));
    AccountId accountId = AccountId.from(123456789L);
    System.out.println(kson.writeJson(accountId));
    var id1 = kson.readJson("1234", new TypeReference<LongId>() {});
    var id2 = kson.readJson("111", new TypeReference<AccountId>() {});
    System.out.println(id1.value());
    System.out.println(id2.getValue());
    System.out.println(
        kson.readJson(kson.writeJson("66666"), new TypeReference<AccountId>() {}).toPlainString());
    var accountState = AccountState.from(AccountStateEnum.INIT);
    var json = kson.writeJson(accountState);
    System.out.println(kson.readJson(json, new TypeReference<AccountState>() {}).getValue());
    var tJson = kson.writeJson(StampedTime.current());
    System.out.println(tJson);
    System.out.println(kson.readJson(tJson, StampedTime.class).toLocalDateTime());
    var account =
        Account.builder()
            .id(AccountId.from(10))
            .state(AccountState.from(AccountStateEnum.ACTIVE))
            .createTime(StampedTime.current())
            .build();
    var s = kson.writeJson(account);
    System.out.println(s);
    System.out.println(kson.readJson(s, Account.class));
    System.out.println(kson.readJson(s, Account.class).getCreateTime());
    var sTime = kson.writeJson(StampedTime.current());
    System.out.println(sTime);
    System.out.println(kson.readJson(sTime, StampedTime.class));
    System.out.println(kson.readJson(sTime, StampedTime.class).toLocalDateTime());
    System.out.println(TenThousandYuan.from(new BigDecimal("110.6")).toPlainString());
  }

  @Test
  void testTTL() throws InterruptedException {
    var executor = Executors.newSingleThreadExecutor();
    var ttlExecutor = TtlExecutors.getTtlExecutor(executor);
    // 先提交一个任务，让线程池启动，创建好线程。
    executor.execute(() -> {});
    var countDownLatch = new CountDownLatch(4);
    var itl = new InheritableThreadLocal<>();
    var ttl = new TransmittableThreadLocal<>();
    final var o = new Object();
    itl.set(o);
    ttl.set(o);
    executor.execute(
        () -> {
          System.out.println(o == itl.get()); // false
          countDownLatch.countDown();
        });
    ttlExecutor.execute(
        () -> {
          System.out.println(o == itl.get()); // false
          countDownLatch.countDown();
        });
    executor.execute(
        () -> {
          System.out.println(o == ttl.get()); // false
          countDownLatch.countDown();
        });
    ttlExecutor.execute(
        () -> {
          System.out.println(o == ttl.get()); // true
          countDownLatch.countDown();
        });
    countDownLatch.await();
  }

  private static void forRun(int time, IntConsumer consumer) {
    var cyclicBarrier = new CyclicBarrier(time);
    var countDownLatch = new CountDownLatch(time);
    IntStream.range(0, time)
        .forEach(
            i -> {
              new Thread(
                      () -> {
                        try {
                          cyclicBarrier.await();
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        } catch (BrokenBarrierException e) {
                          e.printStackTrace();
                        }
                        log.info("Round <{}>", i);
                        consumer.accept(i);
                        countDownLatch.countDown();
                      })
                  .start();
            });
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
