package com.kk.ddd.support.grl;

import java.util.Collections;
import java.util.List;

/**
 * Result of {@link ClusterServer#allMembers()}. <br>
 *
 * @author kingk
 */
public record ClusterMembersResult(ClusterStatus status, List<String> members) {

  public static ClusterMembersResult notRunning() {
    return new ClusterMembersResult(ClusterStatus.NOT_RUNNING, Collections.emptyList());
  }

  public static ClusterMembersResult left(List<String> members) {
    return new ClusterMembersResult(ClusterStatus.LEFT, Collections.unmodifiableList(members));
  }

  public static ClusterMembersResult running(List<String> members) {
    return new ClusterMembersResult(ClusterStatus.RUNNING, Collections.unmodifiableList(members));
  }

  public enum ClusterStatus {
    NOT_RUNNING,
    LEFT,
    RUNNING,
  }
}
