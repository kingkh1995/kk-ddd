package com.kk.ddd.support.util.task;

import com.kk.ddd.support.util.GraphUtils;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * <br>
 * 快速失败机制：所有后置任务完成时都判断是否有任务失败了，并触发complete。<br>
 * 使用同步回调执行触发后置任务，能保证回调的顺序执行。<br>
 * CompletableFuture不支持打断操作，因为它本身并没有与任何线程绑定。
 *
 * @author KaiKoo
 */
@Slf4j
public class AsyncTaskContainer<C> implements AsyncContainer<C> {
  private static final TaskResult FAIL_FAST = TaskResult.fail("fail fast.");

  @Getter private final String name;
  private final String dummy;
  private SimpleDirectedGraph<String, DefaultEdge> graph;
  private List<Function<C, CompletableFuture<TaskResult>>> tasks;
  private Map<String /*taskName*/, Integer /*index*/> index;

  protected AsyncTaskContainer(final String name) {
    this.name = name;
    this.dummy = name;
  }

  protected AsyncTaskContainer(
      String name,
      SimpleDirectedGraph<String, DefaultEdge> graph,
      Map<String, Function<C, CompletableFuture<TaskResult>>> tasks) {
    this.name = name;
    this.graph = Objects.requireNonNull(graph, "graph is null.");
    Objects.requireNonNull(tasks, "tasks is null.");
    if (!Objects.equals(graph.vertexSet(), tasks.keySet())) {
      throw new IllegalArgumentException("graph can't match tasks.");
    }
    var cycles = GraphUtils.findCycles(graph);
    if (Objects.nonNull(cycles) && !cycles.isEmpty()) {
      throw new IllegalArgumentException("circle found in graph, tasks in circle: " + cycles + ".");
    }
    int size = tasks.size() + 1;
    this.tasks = new ArrayList<>(size);
    this.index = new HashMap<>(size);
    tasks.forEach(
        (key, value) -> {
          this.index.put(key, this.tasks.size());
          this.tasks.add(value);
        });
    // init dummy
    String dummy = name;
    while (tasks.containsKey(dummy)) {
      dummy = UUID.randomUUID().toString();
    }
    this.dummy = dummy;
    graph.addVertex(this.dummy);
    this.index.put(this.dummy, this.tasks.size());
    this.tasks.add((context) -> CompletableFuture.completedFuture(TaskResult.succeed()));
    var sJoiner = new StringJoiner(", ", "[", "]");
    var eJoiner = new StringJoiner(", ", "[", "]");
    graph.vertexSet().stream()
        .filter(Predicate.not(Predicate.isEqual(this.dummy)))
        .forEach(
            v -> {
              if (graph.inDegreeOf(v) == 0) {
                graph.addEdge(this.dummy, v);
                sJoiner.add(v);
              }
              if (graph.outDegreeOf(v) == 0) {
                eJoiner.add(v);
              }
            });
    log.info(
        "TaskContainer[{}]{{}} will execute from {} and end with {}.",
        this.name,
        graph.vertexSet().size() - 1,
        sJoiner,
        eJoiner);
  }

  @Override
  public TaskResult execute(C context) {
    try {
      return execute(context, TaskContainers.TIMEOUT);
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      return TaskResult.fail(e.getLocalizedMessage());
    }
  }

  @Override
  public TaskResult execute(C context, int timeout)
      throws ExecutionException, InterruptedException, TimeoutException {
    return new Task().execute(context).get(timeout, TimeUnit.SECONDS);
  }

  private class Task extends CompletableFuture<TaskResult> {
    private final AtomicIntegerArray notifications;
    private volatile boolean failed;

    public Task() {
      this.notifications = new AtomicIntegerArray(tasks.size() - 1);
    }

    CompletableFuture<TaskResult> execute(C context) {
      return signal(dummy, context);
    }

    CompletableFuture<TaskResult> signal(String taskName, C context) {
      var completableFuture = new CompletableFuture<TaskResult>();
      tasks
          .get(index.get(taskName))
          .apply(context) // 获取CompletableFuture，即提交异步任务。
          .whenComplete( // 添加当前任务执行完成后同步回调
              (result, throwable) -> {
                if (failed) { // 快速失败
                  completableFuture.complete(FAIL_FAST);
                  return;
                }
                if (throwable != null || result.failed()) { // 失败场景
                  // 标记快速失败后，直接返回结果，不触发后置任务。
                  failed = true;
                  if (throwable != null) {
                    completableFuture.complete(TaskResult.fail(throwable.getLocalizedMessage()));
                  } else {
                    completableFuture.complete(result);
                  }
                  return;
                }
                // 任务执行成功后触发后置任务
                // 快速失败机制，每个子任务执行完成后都判断是否有任务失败，然后提前complete。
                var subFutures =
                    graph.outgoingEdgesOf(taskName).stream()
                        .map(graph::getEdgeTarget)
                        .filter(
                            successor ->
                                notifications.addAndGet(index.get(successor), 1) // 通知信号加一
                                    == graph.inDegreeOf(successor)) // 后置任务只会被触发一次
                        .map(successor -> signal(successor, context)) // 触发后置任务
                        .peek( // 添加后置任务完成时同步回调
                            signal ->
                                signal.whenComplete(
                                    (sResult, sThrowable) -> {
                                      // 快速失败机制，是自身失败则返回错误信息，否则返回快速失败信息。
                                      if (failed) {
                                        completableFuture.complete(
                                            sResult.failed() ? sResult : FAIL_FAST);
                                      }
                                    }))
                        .toArray(CompletableFuture[]::new);
                // 最后添加全部后置执行成功回调，执行此回调时之前设置的同步回调一定都执行完成了，所以只有全部成功时complete才会成功。
                CompletableFuture.allOf(subFutures)
                    .whenComplete(
                        (ignoredResult, ignoredThrowable) -> completableFuture.complete(result));
              });
      return completableFuture;
    }
  }
}
