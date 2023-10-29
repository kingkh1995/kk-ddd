package com.kk.ddd.support.util.task;

import com.kk.ddd.support.util.GraphUtils;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * <br>
 * 快速失败机制：所有后置任务完成时都判断是否有任务失败了，并触发complete。<br>
 * 使用同步回调执行触发后置任务，能保证回调的顺序执行。<br>
 * todo... 快速失败后取消任务
 *
 * @author KaiKoo
 */
@Slf4j
public class AsyncTaskContainer<C> implements AsyncContainer<C> {

  private static final int DEFAULT_TIMEOUT = 60;
  private static final String DUMMY = "DUMMY";
  private static final TaskResult FAIL_FAST = TaskResult.fail("fail fast.");

  @Getter private final String name;
  private SimpleDirectedGraph<String, DefaultEdge> graph;
  private Map<String, Function<C, CompletableFuture<TaskResult>>> tasks;

  protected AsyncTaskContainer(final String name) {
    this.name = name;
  }

  protected AsyncTaskContainer(
      String name,
      SimpleDirectedGraph<String, DefaultEdge> graph,
      Map<String, Function<C, CompletableFuture<TaskResult>>> tasks) {
    this.name = name;
    this.graph = Objects.requireNonNull(graph, "graph is null.");
    this.tasks = Objects.requireNonNull(tasks, "tasks is null.");
    if (!Objects.equals(graph.vertexSet(), tasks.keySet())) {
      throw new IllegalArgumentException("graph can't match tasks.");
    }
    var cycles = GraphUtils.findCycles(graph);
    if (Objects.nonNull(cycles) && !cycles.isEmpty()) {
      throw new IllegalArgumentException("circle found in graph, tasks in circle: " + cycles + ".");
    }
    if (tasks.containsKey(DUMMY)) {
      throw new IllegalArgumentException("taskName can't be [" + DUMMY + "].");
    }
    graph.addVertex(DUMMY);
    tasks.put(DUMMY, (context) -> CompletableFuture.completedFuture(TaskResult.succeed()));
    graph
        .vertexSet()
        .forEach(
            v -> {
              if (!Objects.equals(DUMMY, v) && graph.inDegreeOf(v) == 0) {
                graph.addEdge(DUMMY, v);
              }
            });
  }

  @Override
  public TaskResult execute(C context) {
    return execute(context, DEFAULT_TIMEOUT);
  }

  @Override
  public TaskResult execute(C context, int timeout) {
    try {
      return new Task().execute(context).get(timeout, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      return TaskResult.fail(e.getLocalizedMessage());
    }
  }

  private class Task extends CompletableFuture<TaskResult> {
    private final Map<String, AtomicInteger> notifications;
    volatile boolean failed;

    public Task() {
      this.notifications = new ConcurrentHashMap<>(tasks.size());
    }

    CompletableFuture<TaskResult> execute(C context) {
      return signal(DUMMY, context);
    }

    CompletableFuture<TaskResult> signal(String taskName, C context) {
      var completableFuture = new CompletableFuture<TaskResult>();
      tasks
          .get(taskName)
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
                // 触发后置任务
                var subFutures =
                    graph.outgoingEdgesOf(taskName).stream()
                        .map(graph::getEdgeTarget)
                        .filter(
                            successor ->
                                notifications
                                        .computeIfAbsent(successor, key -> new AtomicInteger())
                                        .addAndGet(1) // 通知信号加一
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

  public static class Builder<C>
      extends AbstractContainerBuilder<C, CompletableFuture<TaskResult>> {

    protected Builder(String name) {
      super(name);
    }

    @Override
    public Builder<C> addTask(String taskName, Function<C, CompletableFuture<TaskResult>> task) {
      super.addTask(taskName, task);
      return this;
    }

    @Override
    public AsyncTaskContainer<C> buildAsync() {
      return new AsyncTaskContainer<>(name, graph, tasks);
    }
  }
}
