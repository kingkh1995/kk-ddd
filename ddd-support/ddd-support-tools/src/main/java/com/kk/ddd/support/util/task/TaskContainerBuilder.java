package com.kk.ddd.support.util.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * <br>
 * 通用且可重复使用
 *
 * @author KaiKoo
 */
public final class TaskContainerBuilder<C> {
  private final String name;
  private final SimpleDirectedGraph<String, DefaultEdge> graph;
  private final Map<String, Function<C, TaskResult>> tasks;
  private final Map<String, Function<C, CompletableFuture<TaskResult>>> fTasks;

  TaskContainerBuilder(final String name) {
    this.name = name;
    this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
    this.tasks = new HashMap<>();
    this.fTasks = new HashMap<>();
  }

  private void checkTaskName(String taskName) {
    if (Objects.requireNonNull(taskName).isBlank()) {
      throw new IllegalArgumentException();
    }
  }

  private void checkTask(Function<?, ?> task) {
    Objects.requireNonNull(task);
  }

  public TaskContainerBuilder<C> addTask(String taskName, Function<C, TaskResult> task) {
    checkTaskName(taskName);
    checkTask(task);
    if (fTasks.containsKey(taskName) || Objects.nonNull(tasks.putIfAbsent(taskName, task))) {
      throw new IllegalArgumentException("task exists.");
    }
    graph.addVertex(taskName);
    return this;
  }

  public TaskContainerBuilder<C> addFutureTask(
      String taskName, Function<C, CompletableFuture<TaskResult>> task) {
    checkTaskName(taskName);
    checkTask(task);
    if (tasks.containsKey(taskName) || Objects.nonNull(fTasks.putIfAbsent(taskName, task))) {
      throw new IllegalArgumentException("task exists.");
    }
    graph.addVertex(taskName);
    return this;
  }

  public TaskContainerBuilder<C> removeTask(String taskName) {
    checkTaskName(taskName);
    if (Objects.nonNull(tasks.remove(taskName)) || Objects.nonNull(fTasks.remove(taskName))) {
      graph.removeVertex(taskName);
    }
    return this;
  }

  public TaskContainerBuilder<C> addDependsOn(String taskName, String... paths) {
    if (paths.length == 0) {
      return this;
    }
    checkTaskName(taskName);
    if (!tasks.containsKey(taskName) && !fTasks.containsKey(taskName)) {
      throw new IllegalArgumentException("task doesn't exist.");
    }
    for (String dependsOn : paths) {
      checkTaskName(dependsOn);
      if (!tasks.containsKey(dependsOn) && !fTasks.containsKey(dependsOn)) {
        throw new IllegalArgumentException("dependsOn doesn't exist.");
      }
    }
    for (String dependsOn : paths) {
      graph.addEdge(dependsOn, taskName);
    }
    return this;
  }

  public TaskContainerBuilder<C> removeDependsOn(String taskName, String dependsOn) {
    checkTaskName(taskName);
    checkTaskName(dependsOn);
    graph.removeEdge(taskName, dependsOn);
    return this;
  }

  public TaskContainer<C> build() {
    var map = new HashMap<>(tasks);
    fTasks.forEach((key, value) -> map.put(key, c -> value.apply(c).join()));
    return new TaskContainer<>(name, (SimpleDirectedGraph<String, DefaultEdge>) graph.clone(), map);
  }

  public AsyncTaskContainer<C> buildAsync() {
    return buildAsync(TaskContainers.EXECUTOR);
  }

  public AsyncTaskContainer<C> buildAsync(Executor executor) {
    var map = new HashMap<>(fTasks);
    tasks.forEach(
        (key, value) ->
            map.put(key, c -> CompletableFuture.supplyAsync(() -> value.apply(c), executor)));
    return new AsyncTaskContainer<>(
        name, (SimpleDirectedGraph<String, DefaultEdge>) graph.clone(), map);
  }
}
