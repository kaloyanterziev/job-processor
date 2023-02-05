package com.krterziev.jobprocessor.transformers;

import com.krterziev.jobprocessor.models.Task;
import com.krterziev.jobprocessor.payload.request.TasksRequest;
import com.krterziev.jobprocessor.payload.response.TaskResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TaskTransformer {

  private TaskTransformer() {
  }

  public static List<Task> transform(final TasksRequest tasksRequest) {
    return tasksRequest.tasks().stream().map(taskRequest -> new Task(
        taskRequest.name(),
        taskRequest.command(),
        taskRequest.requires() != null
            ? new HashSet<>(taskRequest.requires()) : Collections.emptySet()))
        .toList();
  }

  public static List<TaskResponse> transform(final List<Task> tasks) {
    return tasks.stream().map(task -> new TaskResponse(task.name(), task.command())).toList();
  }

}
