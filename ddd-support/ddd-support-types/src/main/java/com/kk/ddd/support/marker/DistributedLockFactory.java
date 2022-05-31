package com.kk.ddd.support.marker;

import com.kk.ddd.support.util.NameGenerator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 分布式锁工厂 <br>
 *
 * @author KaiKoo
 */
public interface DistributedLockFactory {

  default NameGenerator getLockNameGenerator() {
    return NameGenerator.DEFAULT;
  }

  DistributedLock getLock(@NotBlank String name);

  DistributedLock getMultiLock(@Size(min = 2) List<String> names);
}
