package com.kk.ddd.support.util;

import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;

/**
 * <br>
 *
 * @author KaiKoo
 */
public final class GraphUtils {
  private GraphUtils() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  public static <V> Set<V> findCycles(Graph<V, ?> graph) {
    return new CycleDetector<>(graph).findCycles();
  }
}
