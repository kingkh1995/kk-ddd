package com.kk.ddd.support.raft;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Election implements Runnable {
  static final Vertx vertx = Vertx.vertx();
  final Map<Integer, Integer> peers;
  final int peerId;
  final int port;
  final HttpServer server;
  volatile Role role = Role.CANDIDATE;
  volatile int term = -1;
  volatile int leader = -1;
  volatile int vote = -1;
  volatile Instant heartbeat = Instant.ofEpochMilli(0);
  final ReentrantLock lock = new ReentrantLock();

  public Election(int peerId, int port, String peers) {
    this.peerId = peerId;
    this.port = port;
    this.peers =
        Arrays.stream(peers.split(","))
            .map(s -> s.split("="))
            .map(s -> new int[] {Integer.parseInt(s[0]), Integer.parseInt(s[1])})
            .filter(arr -> arr[0] != peerId)
            .collect(Collectors.toUnmodifiableMap(arr -> arr[0], arr -> arr[1]));
    this.server = initServer();
    this.server.listen(port);
  }

  @Override
  public void run() {
    outer:
    while (true) {
      switch (role) {
        case LEADER:
          {
            int term = this.term;
            for (var entry : peers.entrySet()) {
              if (Role.LEADER != this.role) { // 检查是否时Leader，term可能没变。
                continue outer;
              }
              try {
                sendHeartbeat(entry.getKey(), entry.getValue(), term);
              } catch (Exception e) {
                log.error("{} | Send Heartbeat Error: {}.", this.peerId, e.getMessage(), e);
              }
            }
            sleep(ThreadLocalRandom.current().nextInt(10000));
          }
        case CANDIDATE:
          {
            // 每一个term内，如果是candidate，leader/vote，一个为空，一个不为空。
            // 如果leader和vote都为空，则需要升级term。
            int term, leader, vote;
            boolean flag = false;
            lock.lock();
            try {
              if (Role.LEADER == this.role) {
                continue;
              }
              term = this.term;
              leader = this.leader;
              vote = this.vote;
              if (heartbeat.plus(Duration.ofSeconds(3)).isAfter(Instant.now())) {
                log.info(
                    "{} | Heartbeat check succeed, current term:{}, current leader:{}, current vote:{}.",
                    this.peerId,
                    this.term,
                    this.leader,
                    this.vote);
                flag = true;
              }
            } finally {
              lock.unlock();
            }
            if (flag) {
              sleep(2000);
              continue;
            }
            // 发起选举，休眠随机时间
            sleep(ThreadLocalRandom.current().nextInt(2000));
            if (!lock.tryLock()) {
              continue;
            }
            try {
              if (this.term > term || this.leader != leader || this.vote != vote) {
                continue;
              }
              term = ++this.term;
              this.leader = -1;
              this.vote = this.peerId;
            } finally {
              lock.unlock();
            }
            int count = 1;
            int limit = (peers.size() + 1) >> 1;
            for (var entry : peers.entrySet()) {
              // 只需要检查term或leader，vote是不会变的，因为当前term已经为自己投票了。
              if (this.term > term || this.leader > -1) {
                continue outer;
              }
              try {
                if (vote4me(entry.getKey(), entry.getValue(), term)) {
                  count++;
                }
                if (count > limit) {
                  lock.lock();
                  try {
                    if (this.term == term && this.leader == -1) {
                      log.info("{} | As Leader. ", this.peerId);
                      asLeader();
                    }
                  } finally {
                    lock.unlock();
                  }
                  continue outer;
                }
              } catch (Exception e) {
                log.error("{} | Send vote4me Error: {}. ", this.peerId, e.getMessage(), e);
              }
            }
          }
        case FOLLOWER:
          {
            sleep(30000L);
          }
      }
    }
  }

  private HttpServer initServer() {
    return vertx
        .createHttpServer()
        .requestHandler(
            request -> {
              String uri = request.uri();
              switch (uri) {
                case "/heartbeat":
                  {
                    HttpServerResponse response = request.response();
                    int rTerm = Integer.parseInt(request.getHeader("term"));
                    int rLeader = Integer.parseInt(request.getHeader("leader"));
                    lock.lock();
                    try {
                      // 只要有节点发送心跳，则表示他选主成功，同一个term不会有多个节点发送心跳，因为每个term一个节点只能投一票，故不会选出多个主。
                      if (rTerm >= this.term) {
                        log.info(
                            "{} | Accept {} as Leader, current term:{}, current leader:{}, rTerm:{}. ",
                            this.peerId,
                            rLeader,
                            this.term,
                            this.leader,
                            rTerm);
                        acceptNewTerm(rTerm, rLeader);
                      } else {
                        log.info(
                            "{} | Invalid Heartbeat from {}, current term:{}, current leader:{}, rTerm:{}. ",
                            this.peerId,
                            rLeader,
                            this.term,
                            this.leader,
                            rTerm);
                      }
                      response.putHeader("term", Integer.toString(this.term));
                      response.putHeader("leader", Integer.toString(this.leader));
                    } finally {
                      lock.unlock();
                    }
                    break;
                  }
                case "/vote4me":
                  {
                    HttpServerResponse response = request.response();
                    int rTerm = Integer.parseInt(request.getHeader("term"));
                    int rVote = Integer.parseInt(request.getHeader("vote"));
                    lock.lock();
                    try {
                      if (rTerm > this.term) { // 只有大于当前term时，节点才会为其他节点投票。
                        log.info(
                            "{} | Vote for {}, current term:{}, current leader:{}, current vote:{}, rTerm:{}. ",
                            this.peerId,
                            rVote,
                            this.term,
                            this.leader,
                            this.vote,
                            rTerm);
                        acceptNewVote(rTerm, rVote);
                      } else {
                        log.info(
                            "{} | Refuse Vote4me from {}, current term:{}, current leader:{}, current vote:{}, rTerm:{}. ",
                            this.peerId,
                            rVote,
                            this.term,
                            this.leader,
                            this.vote,
                            rTerm);
                      }
                      response.putHeader("term", Integer.toString(this.term));
                      response.putHeader("leader", Integer.toString(this.leader));
                      response.putHeader("vote", Integer.toString(this.vote));
                    } finally {
                      lock.unlock();
                    }
                    break;
                  }
              }
              request.response().end();
            });
  }

  private void sendHeartbeat(int peerId, int port, int term) throws Exception {
    var httpClient = HttpClient.newHttpClient();
    var request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:" + port + "/heartbeat"))
            .version(HttpClient.Version.HTTP_2)
            .header("term", Integer.toString(term))
            .header("leader", Integer.toString(this.peerId))
            .timeout(Duration.ofSeconds(1))
            .build();
    var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    var rTerm = response.headers().firstValue("term").map(Integer::parseInt).get();
    var rLeader = response.headers().firstValue("leader").map(Integer::parseInt).get();
    lock.lock();
    log.info(
        "{} | Send Heartbeat to {} Return: term:{}, current term:{}, rTerm:{}, rLeader:{}. ",
        this.peerId,
        peerId,
        term,
        this.term,
        rTerm,
        rLeader);
    try {
      if (rTerm > this.term) {
        log.info("{} | Leader exit.", this.peerId);
        // 无需再次判断 term == this.term，可以直接更新term。
        acceptNewTerm(rTerm, rLeader);
      }
    } finally {
      lock.unlock();
    }
  }

  private boolean vote4me(int peerId, int port, int term) throws Exception {
    var httpClient = HttpClient.newHttpClient();
    var request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:" + port + "/vote4me"))
            .version(HttpClient.Version.HTTP_2)
            .header("term", Integer.toString(term))
            .header("vote", Integer.toString(this.peerId))
            .timeout(Duration.ofSeconds(1))
            .build();
    var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    var rTerm = response.headers().firstValue("term").map(Integer::parseInt).get();
    var rLeader = response.headers().firstValue("leader").map(Integer::parseInt).get();
    var rVote = response.headers().firstValue("vote").map(Integer::parseInt).get();
    lock.lock();
    log.info(
        "{} | Send Vote4me to {} Return: term:{}, current term:{}, rTerm:{}, rLeader:{}, rVote:{}. ",
        this.peerId,
        peerId,
        term,
        this.term,
        rTerm,
        rLeader,
        rVote);
    try {
      if (this.term > term || this.leader > -1) {
        return false;
      }
      if (rTerm > term || rLeader > -1) { // 结束选举
        log.info("{} | Vote end.", this.peerId);
        acceptNewTerm(rTerm, rLeader);
        return false;
      }
      return rVote == this.peerId; // 判断是否获得选票
    } finally {
      lock.unlock();
    }
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      log.info("{} | Sleep Interrupted. ", this.peerId);
    }
  }

  private void acceptNewTerm(int term, int leader) {
    // 更新后的term可能未选主成功
    this.role = Role.CANDIDATE;
    this.leader = leader;
    // 未选主成功时，在candidate流程中去增加term。
    this.term = leader > -1 ? term : term - 1;
    // 未选主成功时，通过心跳检测失败去触发发起投票。
    this.heartbeat = leader > -1 ? Instant.now() : Instant.ofEpochMilli(0L);
    this.vote = -1;
  }

  private void acceptNewVote(int term, int vote) {
    this.role = Role.CANDIDATE;
    this.leader = -1;
    this.term = term;
    this.heartbeat = Instant.now();
    this.vote = vote;
  }

  private void asLeader() {
    this.role = Role.LEADER;
    this.leader = this.peerId;
    this.heartbeat = Instant.ofEpochMilli(0L);
    this.vote = -1;
  }

  enum Role {
    LEADER,
    FOLLOWER,
    CANDIDATE
  }
}
