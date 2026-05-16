package com.kk.ddd.support.grl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ketama consistent hash ring. <br>
 * Thread-safe via Copy-On-Write: rebuild() creates a new TreeMap and publishes it atomically
 * through a volatile field. Readers always see a complete, consistent snapshot.
 *
 * @author kingk
 */
public class ConsistentHashRing {

  private static final HashFunction HASHING = Hashing.murmur3_32_fixed();
  private static final String VIRTUAL_SPLITER = "@@";
  private static final int VIRTUAL_NODES = 10;

  private volatile TreeMap<Long, String> ketamaMap = new TreeMap<>();

  private static long hash(String s) {
    return HASHING.hashBytes(s.getBytes(StandardCharsets.UTF_8)).padToLong();
  }

  /**
   * Rebuild the consistent hash ring from a CSV of server addresses.
   *
   * @param serverListCsv comma-separated server addresses, e.g. "ip1:port,ip2:port"
   */
  public void rebuild(String serverListCsv) {
    if (serverListCsv == null || serverListCsv.isBlank()) {
      return;
    }
    this.ketamaMap =
        Arrays.stream(serverListCsv.split(","))
            .flatMap(
                address ->
                    IntStream.range(0, VIRTUAL_NODES)
                        .mapToObj(index -> address + VIRTUAL_SPLITER + index))
            .collect(
                Collectors.toMap(
                    ConsistentHashRing::hash, Function.identity(), (o, n) -> n, TreeMap::new));
  }

  /**
   * Find the server responsible for the given key.
   *
   * @param key the routing key
   * @return server address
   * @throws IllegalStateException if ring is empty
   */
  public String findServer(String key) {
    var map = this.ketamaMap; // volatile read, complete snapshot
    if (map.isEmpty()) {
      throw new IllegalStateException("ConsistentHashRing is empty, rebuild first.");
    }
    var entry = Optional.ofNullable(map.ceilingEntry(hash(key))).orElse(map.firstEntry());
    // both ceilingEntry and firstEntry guarantee non-null on non-empty map
    return entry.getValue().split(VIRTUAL_SPLITER)[0];
  }

  /** Returns the number of physical servers on the ring. */
  public int serverCount() {
    return this.ketamaMap.size() / VIRTUAL_NODES;
  }
}
