package com.kk.ddd.support.util.task;

import com.kk.ddd.support.util.GraphUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
public class TaskFlow<C> extends Task<C> {

  private final AbstractBaseGraph<String, DefaultEdge> graph;
  private final Map<String, Task<FailFastContext<C>>> map;
  private List<Task<FailFastContext<C>>> sorted;
  @Setter private long timeout = 10 * 60 * 1000L;
  @Setter private Executor executor = super.executor();
  private boolean parallel = true;

  protected TaskFlow(
      String name, AbstractBaseGraph<String, DefaultEdge> graph, Map<String, Task<C>> map) {
    super(name);
    this.graph = graph;
    this.map =
        map.entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    Map.Entry::getKey,
                    e -> {
                      var task = e.getValue();
                      if (task instanceof MultiTask<C> multiTask) {
                        return new FailFastMultiTask<>(multiTask);
                      } else {
                        return new FailFastTask<>(task);
                      }
                    }));
    init();
  }

  @Override
  public long timeout() {
    return this.timeout;
  }

  @Override
  public Executor executor() {
    return this.executor;
  }

  public Set<String> taskNames() {
    return this.map.keySet();
  }

  public TaskFlow<C> parallel() {
    parallel = true;
    return this;
  }

  public TaskFlow<C> sequential() {
    parallel = false;
    return this;
  }

  @Override
  protected Consumer<C> action() {
    return context ->
        sorted.stream()
            .map(task -> task.apply(new FailFastContext<>(context)))
            .forEach(CompletableFuture::join);
  }

  @Override
  protected Function<C, CompletableFuture<Void>> asyncAction() {
    // if async action timeout how to fail-fast tasks?
    return context -> {
      if (parallel) {
        return super.asyncAction().apply(context);
      } else {
        return signal(name(), new FailFastContext<>(context, this));
      }
    };
  }

  @Override
  protected Consumer<C> done() {
    return context -> log.info("TaskFlow[{}](parallel:{}) finish.", name(), parallel);
  }

  @Override
  protected BiConsumer<C, Throwable> exceptionally() {
    return (context, throwable) ->
        log.error("TaskFlow[{}](parallel:{}) error.", name(), parallel, throwable);
  }

  private void init() {
    // check dag
    var cycles = GraphUtils.findCycles(graph);
    if (Objects.nonNull(cycles) && !cycles.isEmpty()) {
      throw new IllegalArgumentException("circle found in graph, tasks in circle: " + cycles + ".");
    }
    // check dummy
    if (!map.containsKey(name())) {
      throw new IllegalArgumentException(String.format("task [{%s}] not found", name()));
    }
    graph.removeVertex(name());
    // sequential init
    sorted = new ArrayList<>(map.size());
    var iterator = new TopologicalOrderIterator<>(graph);
    var joiner = new StringJoiner(" => ", "[", "]");
    while (iterator.hasNext()) {
      var taskName = iterator.next();
      sorted.add(map.get(taskName));
      joiner.add(taskName);
    }
    log.info(
        "TaskFlow[{}]{{}} will execute sequential in the following order: {}.",
        name(),
        sorted.size(),
        joiner);
    // parallel init
    graph.addVertex(name());
    var sJoiner = new StringJoiner(", ", "[", "]");
    var eJoiner = new StringJoiner(", ", "[", "]");
    graph.vertexSet().stream()
        .filter(Predicate.not(Predicate.isEqual(name())))
        .forEach(
            v -> {
              if (graph.inDegreeOf(v) == 0) {
                graph.addEdge(name(), v);
                sJoiner.add(v);
              }
              if (graph.outDegreeOf(v) == 0) {
                eJoiner.add(v);
              }
            });
    log.info(
        "TaskFlow[{}]{{}} will execute parallel start from {} and end with {}.",
        name(),
        graph.vertexSet().size() - 1,
        sJoiner,
        eJoiner);
  }

  private CompletableFuture<Void> signal(String taskName, FailFastContext<C> ffc) {
    if (ffc.isFailed()) { // fail fast
      return CompletableFuture.completedFuture(null);
    }
    var future = new CompletableFuture<Void>();
    map.get(taskName)
        .apply(ffc)
        .whenComplete(
            (unused, throwable) -> {
              if (throwable != null) { // error
                ffc.fail();
                future.completeExceptionally(throwable);
              } else if (ffc.isFailed()) { // fail fast
                future.complete(null);
              } else { // signal next
                CompletableFuture.allOf(
                        graph.outgoingEdgesOf(taskName).stream()
                            .map(graph::getEdgeTarget)
                            .filter( // commit successor if signal last
                                successor -> ffc.signal(successor) == graph.inDegreeOf(successor))
                            .map(successor -> signal(successor, ffc)) // recursive call
                            .toArray(CompletableFuture[]::new))
                    .whenComplete( // set callback
                        (u, t) -> {
                          if (t == null) {
                            future.complete(u);
                          } else {
                            future.completeExceptionally(t);
                          }
                        });
              }
            });
    return future;
  }

  public static <C> TaskFlowBuilder<C> newBuilder(final String name) {
    return new TaskFlowBuilder<>(name);
  }
}
