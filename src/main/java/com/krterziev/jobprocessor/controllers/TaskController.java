package com.krterziev.jobprocessor.controllers;

import static com.krterziev.jobprocessor.transformers.TaskTransformer.transform;

import com.krterziev.jobprocessor.models.Task;
import com.krterziev.jobprocessor.payload.request.TasksRequest;
import com.krterziev.jobprocessor.payload.response.TaskResponse;
import com.krterziev.jobprocessor.services.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/tasks")
public class TaskController {

  private final TaskService taskService;

  @Autowired
  public TaskController(final TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping("/sort")
  public List<TaskResponse> sortTasks(@RequestBody final TasksRequest tasksRequest) {
    final List<Task> tasks = taskService.sortTasks(transform(tasksRequest));
    return transform(tasks);
  }
}
