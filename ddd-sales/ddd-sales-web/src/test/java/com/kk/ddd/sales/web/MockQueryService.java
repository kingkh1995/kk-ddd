package com.kk.ddd.sales.web;

/**
 * <br>
 *
 * @author kingk
 */
import com.kk.ddd.support.core.QueryService;
import com.kk.ddd.support.type.LongId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockQueryService implements QueryService<MockEntity, LongId> {

  public static final MockEntity ENTITY =
      MockEntity.builder().id(LongId.of(1L)).name("name").build();

  @Override
  public Optional<MockEntity> find(LongId longId) {
    return Optional.of(ENTITY);
  }

  @Override
  public List<MockEntity> find(Set<LongId> longIds) {
    return null;
  }

  public boolean checkArgs(Object[] args) {
    long target = 1L;
    if (Objects.nonNull(args) && args.length > 0 && args[0] instanceof LongId id) {
      log.info("check equals, target:{}, args:{}", target, id.getValue());
      return id.getValue() == target;
    }
    return true;
  }
}
