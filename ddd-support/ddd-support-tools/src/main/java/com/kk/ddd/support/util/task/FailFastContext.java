package com.kk.ddd.support.util.task;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * <br>
 *
 * @author kingk
 */
class FailFastContext<C> {
  @Getter private final C c;

  @Getter private volatile boolean failed = false;

  private final Map<String, AtomicInteger> signal;

  FailFastContext(final C c) {
    this(c, null);
  }

  FailFastContext(final C c, final TaskFlow<C> flow) {
    this.c = c;
    this.signal =
        Objects.isNull(flow)
            ? Collections.emptyMap()
            : flow.taskNames().stream().collect(Collectors.toMap(s -> s, s -> new AtomicInteger()));
  }

  void fail() {
    failed = true;
  }

  int signal(String name) {
    return Optional.ofNullable(signal)
        .map(m -> m.get(name))
        .map(AtomicInteger::incrementAndGet)
        .orElse(-1);
  }
}
