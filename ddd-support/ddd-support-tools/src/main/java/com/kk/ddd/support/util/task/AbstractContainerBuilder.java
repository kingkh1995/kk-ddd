package com.kk.ddd.support.util.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * <br>
 * todo... redesign
 *
 * @author KaiKoo
 */
public abstract class AbstractContainerBuilder<C, T> {
  protected final String name;
  protected final SimpleDirectedGraph<String, DefaultEdge> graph;
  protected final Map<String, Function<C, T>> tasks;

  protected AbstractContainerBuilder(final String name) {
    this.name = name;
    this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
    this.tasks = new HashMap<>();
  }

  protected void checkTaskName(String taskName) {
    if (Objects.requireNonNull(taskName).isBlank()) {
      throw new IllegalArgumentException();
    }
  }

  protected void checkTask(Function<C, T> task) {
    Objects.requireNonNull(task);
  }

  public AbstractContainerBuilder<C, T> addTask(String taskName, Function<C, T> task) {
    checkTaskName(taskName);
    checkTask(task);
    if (Objects.nonNull(tasks.putIfAbsent(taskName, task))) {
      throw new IllegalArgumentException("task exists.");
    }
    graph.addVertex(taskName);
    return this;
  }

  public AbstractContainerBuilder<C, T> removeTask(String taskName) {
    checkTaskName(taskName);
    if (Objects.nonNull(tasks.remove(taskName))) {
      graph.removeVertex(taskName);
    }
    return this;
  }

  public AbstractContainerBuilder<C, T> addDependsOn(String taskName, String dependsOn) {
    checkTaskName(taskName);
    checkTaskName(dependsOn);
    if (!tasks.containsKey(taskName)) {
      throw new IllegalArgumentException("task doesn't exist.");
    }
    if (!tasks.containsKey(dependsOn)) {
      throw new IllegalArgumentException("dependsOn doesn't exist.");
    }
    graph.addEdge(dependsOn, taskName);
    return this;
  }

  public AbstractContainerBuilder<C, T> removeDependsOn(String taskName, String dependsOn) {
    checkTaskName(taskName);
    checkTaskName(dependsOn);
    graph.removeEdge(taskName, dependsOn);
    return this;
  }

  public TaskContainer<C> build() {
    throw new UnsupportedOperationException();
  }

  public AsyncTaskContainer<C> buildAsync() {
    throw new UnsupportedOperationException();
  }
}
