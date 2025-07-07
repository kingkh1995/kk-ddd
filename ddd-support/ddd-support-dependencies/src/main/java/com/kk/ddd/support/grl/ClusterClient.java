package com.kk.ddd.support.grl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <br>
 *
 * @author mm
 */
public class ClusterClient {

  private static final HashFunction HASHING = Hashing.murmur3_32_fixed();
  private static final String VIRTUAL_SPLITER = "@@";

  private final ClusterClientConfig clientConfig;
  private final ArrayList<String> servers;
  protected TreeMap<Long, String> ketamaMap;
  private long lastCheckTimestamp;

  public ClusterClient(ClusterClientConfig clientConfig) {
    this.clientConfig = clientConfig;
    this.servers = new ArrayList<>(clientConfig.getServers());
  }

  private static long hash(String s) {
    return HASHING.hashBytes(s.getBytes()).asLong();
  }

  private static String getServers(String address) {
    // todo..
    return null;
  }

  protected void doCheckServer() {
    if (System.currentTimeMillis() - lastCheckTimestamp < clientConfig.getCheckInterval()) {
      return;
    }
    Collections.shuffle(this.servers);
    this.servers.stream()
        .map(ClusterClient::getServers)
        .findAny()
        .ifPresent(
            ret -> {
              ketamaMap =
                  Arrays.stream(ret.split(","))
                      .flatMap(
                          address ->
                              IntStream.range(0, 10)
                                  .mapToObj(index -> address + VIRTUAL_SPLITER + index))
                      .collect(
                          Collectors.toMap(
                              ClusterClient::hash, Function.identity(), (o, n) -> n, TreeMap::new));
              lastCheckTimestamp = System.currentTimeMillis();
            });
  }

  protected String findServer(String key) {
    return Optional.ofNullable(ketamaMap.ceilingEntry(hash(key)))
        .orElse(ketamaMap.firstEntry())
        .getValue()
        .split(VIRTUAL_SPLITER)[0];
  }
}
