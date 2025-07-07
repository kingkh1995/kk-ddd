package com.kk.ddd.support.grl;

import java.util.List;
import lombok.Data;

/**
 * <br>
 *
 * @author mm
 */
@Data
public class ClusterClientConfig {
  private List<String> servers;
  private int checkInterval = 20_000;
}
