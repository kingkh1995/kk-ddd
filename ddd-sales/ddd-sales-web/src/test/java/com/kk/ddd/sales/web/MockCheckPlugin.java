package com.kk.ddd.sales.web;

import com.kk.ddd.support.access.AccessConditionCheckPlugin;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
@Component
public class MockCheckPlugin implements AccessConditionCheckPlugin {
  @Override
  public boolean canAccess(Object obj, String args) {
    if (obj instanceof Optional<?> optional
        && optional.isPresent()
        && optional.get() instanceof MockEntity entity) {
      log.info("check equals, arg:{}, result:{}", args, entity.getName());
      return Objects.equals(args, entity.getName());
    }
    return true;
  }

  @Override
  public String getIdentifier() {
    return "mock";
  }
}
