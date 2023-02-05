package com.krterziev.jobprocessor.controllers;

import static com.krterziev.jobprocessor.transformers.TaskTransformer.transform;

import com.krterziev.jobprocessor.exceptions.CircularDependencyDetectedException;
import com.krterziev.jobprocessor.models.Task;
import com.krterziev.jobprocessor.payload.request.TasksRequest;
import com.krterziev.jobprocessor.payload.response.TaskResponse;
import com.krterziev.jobprocessor.services.TaskService;
import com.krterziev.jobprocessor.transformers.BashScriptTransformer;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<List<TaskResponse>> sortTasks(
      @RequestBody final TasksRequest tasksRequest) throws CircularDependencyDetectedException {
    final List<Task> tasks = taskService.sortTasks(transform(tasksRequest));
    return ResponseEntity.ok(transform(tasks));
  }

  @PostMapping(
      value = "/sort")
  public ResponseEntity<Resource> sortTasksAndReturnBashScript(
      @RequestBody final TasksRequest tasksRequest)
      throws IOException, CircularDependencyDetectedException {
    final List<Task> tasks = taskService.sortTasks(transform(tasksRequest));
    final InputStream inputStream = BashScriptTransformer.transform(tasks);
    final InputStreamResource resource = new InputStreamResource(inputStream);
    return ResponseEntity.ok()
        .contentLength(inputStream.available())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }
}
