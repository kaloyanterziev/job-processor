package com.krterziev.jobprocessor.models;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TasksGraph {

  private final Map<Task, List<Task>> adjacencyList = new HashMap<>();
  private final List<Task> tasks;

  public TasksGraph(List<Task> tasks) {
    this.tasks = tasks;
    final Map<String, Task> nameToTask = tasks.stream()
        .collect(Collectors.toMap(Task::name, Function.identity()));

    for (final Task task : tasks) {
      for (String prerequisiteTaskName : task.requires()) {
        final Task prerequisiteTask = nameToTask.get(prerequisiteTaskName);
        adjacencyList.putIfAbsent(prerequisiteTask, new ArrayList<>());
        adjacencyList.get(prerequisiteTask).add(task);
      }
    }
  }

  public List<Task> sort() {
    final List<Task> result = new ArrayList<>();
    final Queue<Task> taskQueue = new ArrayDeque<>();
    for (final Task task : tasks) {
      if (task.requires().size() == 0) {
        taskQueue.add(task);
      }
    }

    while (!taskQueue.isEmpty()) {
      final Task task = taskQueue.poll();
      result.add(task);
      for (final Task adjTask : adjacencyList.get(task)) {
        adjTask.requires().remove(task.name());
        if (adjTask.requires().size() == 0) {
          taskQueue.add(adjTask);
        }
      }
    }
    if (result.size() != tasks.size()) {
      return new ArrayList<>();
    }
    return result;
  }
}
