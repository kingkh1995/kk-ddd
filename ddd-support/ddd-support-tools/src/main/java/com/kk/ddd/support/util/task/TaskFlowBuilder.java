package com.kk.ddd.support.util.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * <br>
 *
 * @author kingk
 */
public class TaskFlowBuilder<C> {

  private final String name;
  private final SimpleDirectedGraph<String, DefaultEdge> graph;
  private final Map<String, Task<C>> map;

  TaskFlowBuilder(final String name) {
    this(name, new Task.EmptyTask<>(name));
  }

  TaskFlowBuilder(final String name, final Task<C> initTask) {
    this.name = name;
    this.map = new HashMap<>();
    this.map.put(name, initTask);
    this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
  }

  @SafeVarargs
  public final TaskFlowBuilder<C> add(Task<C> task, Task<C>... dependsOns) {
    putIfAbsent(task);
    Arrays.stream(dependsOns)
        .filter(Predicate.not(dependsOn -> Objects.equals(dependsOn, task))) // exclude self circle
        .forEach(
            dependsOn -> {
              putIfAbsent(dependsOn);
              graph.addEdge(dependsOn.name(), task.name());
            });
    return this;
  }

  public final TaskFlowBuilder<C> addHead(Task<C> task) {
    putIfAbsent(task);
    graph.vertexSet().stream()
        .filter(Predicate.not(v -> Objects.equals(v, task.name()))) // exclude self circle
        .forEach(
            v -> {
              graph.removeAllEdges(v, task.name()); // remove dependsOn
              graph.addEdge(task.name(), v);
            });
    return this;
  }

  public final TaskFlowBuilder<C> addTail(Task<C> task) {
    putIfAbsent(task);
    graph.vertexSet().stream()
        .filter(Predicate.not(v -> Objects.equals(v, task.name()))) // exclude self circle
        .forEach(
            v -> {
              graph.removeAllEdges(task.name(), v); // remove dependsOn
              graph.addEdge(v, task.name());
            });
    return this;
  }

  public final TaskFlowBuilder<C> remove(Task<C> task) {
    if (map.remove(task.name(), task)) {
      graph.removeVertex(task.name());
    }
    return this;
  }

  private void putIfAbsent(Task<C> task) {
    var old = map.putIfAbsent(Objects.requireNonNull(task).name(), task);
    if (Objects.isNull(old)) {
      graph.addVertex(task.name());
    } else if (!Objects.equals(old, task)) { // duplicate check
      throw new IllegalArgumentException("duplicate tasks");
    }
  }

  public TaskFlow<C> build() {
    return new TaskFlow<>(
        name,
        (AbstractBaseGraph<String, DefaultEdge>) graph.clone(),
        Collections.unmodifiableMap(this.map));
  }

  public TaskFlow<C> buildSequential() {
    return build().sequential();
  }

  public TaskFlow<C> buildParallel() {
    return build().parallel();
  }
}
