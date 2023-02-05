package com.krterziev.jobprocessor.controllers;

import static com.krterziev.jobprocessor.matchers.ResponseBodyMatchers.responseBody;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krterziev.jobprocessor.exceptions.CircularDependencyDetectedException;
import com.krterziev.jobprocessor.models.Task;
import com.krterziev.jobprocessor.payload.request.TaskRequest;
import com.krterziev.jobprocessor.payload.request.TasksRequest;
import com.krterziev.jobprocessor.payload.response.TaskResponse;
import com.krterziev.jobprocessor.services.TaskService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = TaskController.class)
public class TaskControllerTest {

  private static final String TASK_1_NAME = "task-1";
  private static final String TASK_2_NAME = "task-2";
  private static final String TASK_3_NAME = "task-3";
  private static final String TASK_COMMAND = "task command";
  private static final List<String> TASK_REQUIREMENTS = Arrays.asList(TASK_1_NAME, TASK_2_NAME);

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  private TaskService service;

  @Test
  void givenTasks_whenSorting_thenReturnTasksSorted()
      throws Exception {
    final TasksRequest tasksRequest = givenTasksRequest();

    final List<Task> unsortedTasks = givenUnsortedTasks();
    final List<Task> tasks = givenSortedTasks();
    when(service.sortTasks(unsortedTasks)).thenReturn(tasks);

    final List<TaskResponse> expectedTasksResponse = givenTasksResponse();
    mvc.perform(get("/tasks/sort")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(tasksRequest)))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectsAsJson(expected(expectedTasksResponse), TaskResponse.class));
  }

  @Test
  void givenTasksWithCircularDependency_whenSorting_thenReturnBadRequest()
      throws Exception {
    final TasksRequest tasksRequest = givenTasksRequestWithCircularDependency();

    final List<Task> unsortedTasks = givenUnsortedTasksWithCircularDependency();
    when(service.sortTasks(unsortedTasks)).thenThrow(new CircularDependencyDetectedException());


    mvc.perform(get("/tasks/sort")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(tasksRequest)))
        .andExpect(status().isBadRequest());
  }

  private List<Task> givenUnsortedTasksWithCircularDependency() {
    return Arrays.asList(
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.singleton(TASK_1_NAME)),
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.singleton(TASK_2_NAME)),
        new Task(TASK_3_NAME, TASK_COMMAND, Collections.emptySet()));
  }

  private TasksRequest givenTasksRequestWithCircularDependency() {
    return new TasksRequest(Arrays.asList(
        new TaskRequest(TASK_2_NAME, TASK_COMMAND, Collections.singletonList(TASK_1_NAME)),
        new TaskRequest(TASK_1_NAME, TASK_COMMAND, Collections.singletonList(TASK_2_NAME)),
        new TaskRequest(TASK_3_NAME, TASK_COMMAND, Collections.emptyList())));
  }

  private List<Task> givenSortedTasks() {
    return Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, new HashSet<>(TASK_REQUIREMENTS)));
  }

  private List<Task> givenUnsortedTasks() {
    return Arrays.asList(
        new Task(TASK_3_NAME, TASK_COMMAND, new HashSet<>(TASK_REQUIREMENTS)),
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()));
  }

  private TasksRequest givenTasksRequest() {
    return new TasksRequest(Arrays.asList(
        new TaskRequest(TASK_3_NAME, TASK_COMMAND, TASK_REQUIREMENTS),
        new TaskRequest(TASK_1_NAME, TASK_COMMAND, Collections.emptyList()),
        new TaskRequest(TASK_2_NAME, TASK_COMMAND, Collections.emptyList())));
  }

  private List<TaskResponse> givenTasksResponse() {
    return Arrays.asList(
        new TaskResponse(TASK_1_NAME, TASK_COMMAND),
        new TaskResponse(TASK_2_NAME, TASK_COMMAND),
        new TaskResponse(TASK_3_NAME, TASK_COMMAND));
  }

  private static List<Object> expected(final List<TaskResponse> boards) {
    return boards.stream().map(b -> (Object) b).toList();
  }



}
