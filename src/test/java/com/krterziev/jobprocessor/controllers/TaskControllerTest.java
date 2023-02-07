package com.krterziev.jobprocessor.controllers;

import static com.krterziev.jobprocessor.matchers.ResponseBodyMatchers.responseBody;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krterziev.jobprocessor.exceptions.CircularDependencyDetectedException;
import com.krterziev.jobprocessor.models.Task;
import com.krterziev.jobprocessor.payload.request.TaskRequest;
import com.krterziev.jobprocessor.payload.request.TasksRequest;
import com.krterziev.jobprocessor.payload.response.TaskResponse;
import com.krterziev.jobprocessor.services.TaskService;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = TaskController.class)
public class TaskControllerTest {

  private static final String TASK_1_NAME = "task-1";
  private static final String TASK_2_NAME = "task-2";
  private static final String TASK_3_NAME = "task-3";
  private static final String TASK_1_COMMAND = "task command 1";
  private static final String TASK_2_COMMAND = "task command 2";
  private static final String TASK_3_COMMAND = "task command 3";
  private final static String BASH_COMMENT = "#!/usr/bin/env bash";
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
    mvc.perform(post("/tasks/sort")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(tasksRequest)))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectsAsJson(expected(expectedTasksResponse),
            TaskResponse.class));

    verify(service, times(1)).sortTasks(unsortedTasks);
  }

  @Test
  void givenTasks_whenSortingToBashScript_thenReturnTasksSorted()
      throws Exception {
    final TasksRequest tasksRequest = givenTasksRequest();

    final List<Task> unsortedTasks = givenUnsortedTasks();
    final List<Task> tasks = givenSortedTasks();
    when(service.sortTasks(unsortedTasks)).thenReturn(tasks);

    final String expectedTasksResponse = givenTasksResponseAsBashScript();
    mvc.perform(post("/tasks/sort-commands")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(tasksRequest)))
        .andExpect(status().isOk())
        .andExpect(content().bytes(expectedTasksResponse.getBytes(StandardCharsets.UTF_8)));

    verify(service, times(1)).sortTasks(unsortedTasks);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/tasks/sort", "/tasks/sort-commands"})
  void givenTasksWithCircularDependency_whenSorting_thenReturnBadRequest(final String route)
      throws Exception {
    final TasksRequest tasksRequest = givenTasksRequestWithCircularDependency();

    final List<Task> unsortedTasks = givenUnsortedTasksWithCircularDependency();
    when(service.sortTasks(unsortedTasks)).thenThrow(new CircularDependencyDetectedException());

    mvc.perform(post(route)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(tasksRequest)))
        .andExpect(status().isBadRequest());

    verify(service, times(1)).sortTasks(unsortedTasks);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/tasks/sort", "/tasks/sort-commands"})
  void givenTasksWithInvalidPrerequisites_whenSorting_thenReturnBadRequest(final String route)
      throws Exception {
    final TasksRequest tasksRequest = givenTasksRequestWithInvalidPrerequisites();

    mvc.perform(post(route)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(tasksRequest)))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(service);
  }

  private TasksRequest givenTasksRequestWithInvalidPrerequisites() {
    return new TasksRequest(Arrays.asList(
        new TaskRequest(TASK_2_NAME, TASK_2_COMMAND,
            Collections.singletonList("invalid-task-name")),
        new TaskRequest(TASK_1_NAME, TASK_1_COMMAND, Collections.singletonList(TASK_2_NAME)),
        new TaskRequest(TASK_3_NAME, TASK_3_COMMAND, Collections.emptyList())));
  }

  private List<Task> givenUnsortedTasksWithCircularDependency() {
    return Arrays.asList(
        new Task(TASK_2_NAME, TASK_2_COMMAND, Collections.singleton(TASK_1_NAME)),
        new Task(TASK_1_NAME, TASK_1_COMMAND, Collections.singleton(TASK_2_NAME)),
        new Task(TASK_3_NAME, TASK_3_COMMAND, Collections.emptySet()));
  }

  private TasksRequest givenTasksRequestWithCircularDependency() {
    return new TasksRequest(Arrays.asList(
        new TaskRequest(TASK_2_NAME, TASK_2_COMMAND, Collections.singletonList(TASK_1_NAME)),
        new TaskRequest(TASK_1_NAME, TASK_1_COMMAND, Collections.singletonList(TASK_2_NAME)),
        new TaskRequest(TASK_3_NAME, TASK_3_COMMAND, Collections.emptyList())));
  }

  private List<Task> givenSortedTasks() {
    return Arrays.asList(
        new Task(TASK_1_NAME, TASK_1_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_2_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_3_COMMAND, new HashSet<>(TASK_REQUIREMENTS)));
  }

  private List<Task> givenUnsortedTasks() {
    return Arrays.asList(
        new Task(TASK_3_NAME, TASK_3_COMMAND, new HashSet<>(TASK_REQUIREMENTS)),
        new Task(TASK_1_NAME, TASK_1_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_2_COMMAND, Collections.emptySet()));
  }

  private TasksRequest givenTasksRequest() {
    return new TasksRequest(Arrays.asList(
        new TaskRequest(TASK_3_NAME, TASK_3_COMMAND, TASK_REQUIREMENTS),
        new TaskRequest(TASK_1_NAME, TASK_1_COMMAND, Collections.emptyList()),
        new TaskRequest(TASK_2_NAME, TASK_2_COMMAND, Collections.emptyList())));
  }

  private List<TaskResponse> givenTasksResponse() {
    return Arrays.asList(
        new TaskResponse(TASK_1_NAME, TASK_1_COMMAND),
        new TaskResponse(TASK_2_NAME, TASK_2_COMMAND),
        new TaskResponse(TASK_3_NAME, TASK_3_COMMAND));
  }

  private String givenTasksResponseAsBashScript() {
    return BASH_COMMENT + "\n\n"
        + TASK_1_COMMAND + "\n"
        + TASK_2_COMMAND + "\n"
        + TASK_3_COMMAND + "\n";
  }

  private static List<Object> expected(final List<TaskResponse> boards) {
    return boards.stream().map(b -> (Object) b).toList();
  }


}
