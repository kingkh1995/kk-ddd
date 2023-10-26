package com.kk.ddd.support.util.task;

import java.util.*;
import java.util.function.Function;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * 同步任务容器，支持定义任务图（DAG)，执行时按拓扑排序同步执行。 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class TaskContainer<C> implements Container<C> {

  @Getter private final String name;
  private SimpleDirectedGraph<String, DefaultEdge> graph;
  private List<Function<C, TaskResult>> sortedTasks;

  /** for Empty subClass */
  protected TaskContainer(String name) {
    this.name = name;
  }

  private TaskContainer(
      final String name,
      final SimpleDirectedGraph<String, DefaultEdge> graph,
      final Map<String, Function<C, TaskResult>> tasks) {
    // check args
    this.name = Objects.requireNonNull(name, "name is null.");
    this.graph = Objects.requireNonNull(graph, "graph is null.");
    Objects.requireNonNull(tasks, "tasks is null.");
    if (!Objects.equals(this.graph.vertexSet(), tasks.keySet())) {
      throw new IllegalArgumentException("graph can't match tasks.");
    }
    // check dag
    var cycles = new CycleDetector<>(graph).findCycles();
    if (Objects.nonNull(cycles) && !cycles.isEmpty()) {
      throw new IllegalArgumentException("circle found in graph, tasks in circle: " + cycles + ".");
    }
    // generate topological order
    this.sortedTasks = new ArrayList<>(tasks.size());
    var iterator = new TopologicalOrderIterator<>(graph);
    var joiner = new StringJoiner(" => ", "[", "]");
    while (iterator.hasNext()) {
      var taskName = iterator.next();
      this.sortedTasks.add(tasks.get(taskName));
      joiner.add(taskName);
    }
    log.info("TaskContainer[{}] will execute in the following order: {}.", this.name, joiner);
  }

  @Override
  public TaskResult execute(C context) {
    return sortedTasks.stream()
        .map(f -> f.apply(context))
        .filter(TaskResult::failed)
        .findAny()
        .orElseGet(TaskResult::succeed);
  }

  public static class Builder<C> {
    private final String name;
    private final SimpleDirectedGraph<String, DefaultEdge> graph;
    private final Map<String, Function<C, TaskResult>> tasks;

    protected Builder(final String name) {
      this.name = name;
      this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
      this.tasks = new HashMap<>();
    }

    public Builder<C> addTask(String taskName, Function<C, TaskResult> task) {
      if (Objects.nonNull(tasks.putIfAbsent(taskName, task))) {
        throw new IllegalArgumentException("task exists.");
      }
      graph.addVertex(taskName);
      return this;
    }

    public Builder<C> removeTask(String taskName) {
      if (Objects.nonNull(tasks.remove(taskName))) {
        graph.removeVertex(taskName);
      }
      return this;
    }

    public Builder<C> addDependsOn(String taskName, String dependsOn) {
      if (!tasks.containsKey(taskName)) {
        throw new IllegalArgumentException("task doesn't exist.");
      }
      if (!tasks.containsKey(dependsOn)) {
        throw new IllegalArgumentException("dependsOn doesn't exist.");
      }
      graph.addEdge(dependsOn, taskName);
      return this;
    }

    public Builder<C> removeDependsOn(String taskName, String dependsOn) {
      graph.removeEdge(taskName, dependsOn);
      return this;
    }

    public TaskContainer<C> build() {
      return new TaskContainer<>(name, graph, tasks);
    }
  }
}
