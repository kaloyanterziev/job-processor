package com.krterziev.jobprocessor.scheduling;

import com.krterziev.jobprocessor.exceptions.CircularDependencyDetectedException;
import com.krterziev.jobprocessor.models.Task;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class TasksGraph {

  private final Map<String, List<Task>> adjacencyList = new HashMap<>();
  private final List<Task> tasks;

  public TasksGraph(List<Task> tasks) {
    this.tasks = tasks;

    for (final Task task : tasks) {
      for (String prerequisiteTaskName : task.requires()) {
        adjacencyList.putIfAbsent(prerequisiteTaskName, new ArrayList<>());
        adjacencyList.get(prerequisiteTaskName).add(task);
      }
    }
  }

  public List<Task> sort() throws CircularDependencyDetectedException {
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
      for (final Task adjTask : adjacencyList.getOrDefault(task.name(), Collections.emptyList())) {
        adjTask.requires().remove(task.name());
        if (adjTask.requires().size() == 0) {
          taskQueue.add(adjTask);
        }
      }
    }
    if (result.size() != tasks.size()) {
      throw new CircularDependencyDetectedException();
    }
    return result;
  }
}
