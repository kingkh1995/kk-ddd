package com.kk.ddd.support.util.task;

import com.kk.ddd.support.util.GraphUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import lombok.Getter;
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
                Collectors.toMap(
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
    return context -> {
      var ffc = new FailFastContext<>(context);
      // no need to fail fast before submit, just fail fast if throw or cancel or timeout
      sorted.stream()
          .map(task -> task.apply(ffc))
          .peek(future -> future.whenComplete(ffc::failFast))
          .forEach(CompletableFuture::join);
    };
  }

  @Override
  protected Function<C, CompletableFuture<Void>> asyncAction() {
    if (!parallel) {
      return super.asyncAction();
    }
    return context -> {
      var pffc = new ParallelFailFastContext<>(context, this.map);
      return signal(name(), pffc).whenComplete(pffc::failFast);
    };
  }

  @Override
  protected void whenCompleteAction(C context, Throwable throwable) {
    if (throwable == null) {
      log.info("TaskFlow[{}](parallel:{}) finish.", name(), parallel);
    } else {
      log.error("TaskFlow[{}](parallel:{}) error.", name(), parallel, throwable);
    }
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

  private CompletableFuture<Void> signal(String taskName, ParallelFailFastContext<C> pffc) {
    if (pffc.isFailed()) { // fail fast
      return CompletableFuture.completedFuture(null);
    }
    var future = new CompletableFuture<Void>();
    map.get(taskName)
        .apply(pffc)
        .whenComplete(
            (unused, throwable) -> {
              if (throwable != null) { // error
                pffc.fail();
                future.completeExceptionally(throwable);
              } else if (pffc.isFailed()) { // fail fast
                future.complete(null);
              } else { // signal next
                CompletableFuture.allOf(
                        graph.outgoingEdgesOf(taskName).stream()
                            .map(graph::getEdgeTarget)
                            .filter( // commit successor if signal last
                                successor -> pffc.signal(successor) == graph.inDegreeOf(successor))
                            .map(successor -> signal(successor, pffc)) // recursive call
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

  /*
  fail fast support
   */
  @Getter
  static class FailFastContext<C> {
    private final C c;

    private volatile boolean failed = false;

    public FailFastContext(final C c) {
      this.c = c;
    }

    public void fail() {
      failed = true;
    }

    public void failFast(Void unused, Throwable throwable) {
      if (throwable != null) {
        fail();
      }
    }
  }

  static class ParallelFailFastContext<C> extends FailFastContext<C> {

    private final Map<String, AtomicInteger> signal;

    public ParallelFailFastContext(final C c, final Map<String, Task<FailFastContext<C>>> map) {
      super(c);
      this.signal =
          map.keySet().stream().collect(Collectors.toMap(s -> s, s -> new AtomicInteger()));
    }

    public int signal(String name) {
      return signal.get(name).incrementAndGet();
    }
  }

  static class FailFastTask<C> extends Task<FailFastContext<C>> {

    private final Task<C> task;

    protected FailFastTask(Task<C> task) {
      super(task.name());
      this.task = task;
    }

    @Override
    protected Consumer<FailFastContext<C>> action() {
      return ffc -> {
        if (ffc.isFailed()) { // fail fast before execute
          log.info("Task[{}] cancel.", task.name());
          return;
        }
        task.action().accept(ffc.getC());
      };
    }

    @Override
    public long timeout() {
      return task.timeout();
    }

    @Override
    public Executor executor() {
      return task.executor();
    }

    @Override
    protected void whenCompleteAction(FailFastContext<C> ffc, Throwable throwable) {
      task.whenCompleteAction(ffc.getC(), throwable);
    }
  }

  static class FailFastMultiTask<C> extends MultiTask<FailFastContext<C>> {

    private final MultiTask<C> task;

    protected FailFastMultiTask(MultiTask<C> task) {
      super(task.name());
      this.task = task;
    }

    @Override
    protected ToIntFunction<FailFastContext<C>> getCount() {
      return ffc -> task.getCount().applyAsInt(ffc.getC());
    }

    @Override
    protected ObjIntConsumer<FailFastContext<C>> subAction() {
      return (ffc, i) -> {
        if (ffc.isFailed()) { // fail fast before execute
          log.info("MultiTask[{}]({}) cancel.", task.name(), i);
          return;
        }
        task.subAction().accept(ffc.getC(), i);
      };
    }

    @Override
    public long timeout() {
      return task.timeout();
    }

    @Override
    public Executor executor() {
      return task.executor();
    }

    @Override
    protected void whenCompleteAction(FailFastContext<C> ffc, Throwable throwable) {
      task.whenCompleteAction(ffc.getC(), throwable);
    }

    @Override
    protected void whenSubCompleteAction(FailFastContext<C> ffc, int index, Throwable throwable) {
      task.whenSubCompleteAction(ffc.getC(), index, throwable);
    }
  }
}
