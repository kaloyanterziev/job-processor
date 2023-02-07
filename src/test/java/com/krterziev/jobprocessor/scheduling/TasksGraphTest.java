package com.krterziev.jobprocessor.scheduling;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.krterziev.jobprocessor.exceptions.CircularDependencyDetectedException;
import com.krterziev.jobprocessor.models.Task;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class TasksGraphTest {

  private static final String TASK_1_NAME = "task-1";
  private static final String TASK_2_NAME = "task-2";
  private static final String TASK_3_NAME = "task-3";
  private static final String TASK_4_NAME = "task-4";
  private static final String TASK_5_NAME = "task-5";
  private static final String TASK_6_NAME = "task-6";
  private static final String TASK_7_NAME = "task-7";
  private static final String TASK_COMMAND = "task command";


  @Test
  void givenZeroTasks_returnZeroTasks() throws CircularDependencyDetectedException {

    final TasksGraph graph = new TasksGraph(Collections.emptyList());

    assertThat(graph.sort(), equalTo(Collections.emptyList()));
  }

  @Test
  void givenOneTasks_returnTheSameTask() throws CircularDependencyDetectedException {
    final List<Task> tasks = Collections.singletonList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()));

    final TasksGraph graph = new TasksGraph(tasks);

    assertThat(graph.sort(), equalTo(tasks));
  }

  @Test
  void givenUnsortedMultipleTasks_thenSortInOrder() throws CircularDependencyDetectedException {
    final Set<String> taskThreePrerequisites = new HashSet<>(
        Arrays.asList(TASK_1_NAME, TASK_2_NAME));

    final List<Task> tasks = Arrays.asList(
        new Task(TASK_3_NAME, TASK_COMMAND, taskThreePrerequisites),
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()));

    final TasksGraph graph = new TasksGraph(tasks);

    final List<Task> expectedTasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, Collections.emptySet()));

    assertThat(graph.sort(), equalTo(expectedTasks));
  }

  @Test
  void givenSortedMultipleTasks_thenSortInOrder() throws CircularDependencyDetectedException {
    final Set<String> taskThreePrerequisites = new HashSet<>(
        Arrays.asList(TASK_1_NAME, TASK_2_NAME));

    final List<Task> tasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, taskThreePrerequisites));

    final TasksGraph graph = new TasksGraph(tasks);

    final List<Task> expectedTasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, Collections.emptySet()));

    assertThat(graph.sort(), equalTo(expectedTasks));
  }

  @Test
  void givenMultipleTasksWithPrerequisitesContainingCircularDependency_thenSortInOrder() {
    final Set<String> taskTwoPrerequisites = new HashSet<>(Arrays.asList(TASK_3_NAME));
    final Set<String> taskThreePrerequisites = new HashSet<>(Arrays.asList(TASK_2_NAME));
    final Set<String> taskFourPrerequisites = new HashSet<>(
        Arrays.asList(TASK_2_NAME, TASK_3_NAME));

    final List<Task> tasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, taskTwoPrerequisites),
        new Task(TASK_3_NAME, TASK_COMMAND, taskThreePrerequisites),
        new Task(TASK_4_NAME, TASK_COMMAND, taskFourPrerequisites));

    final TasksGraph graph = new TasksGraph(tasks);

    final List<Task> expectedTasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_4_NAME, TASK_COMMAND, Collections.emptySet()));

    assertThrows(CircularDependencyDetectedException.class, graph::sort);
  }

  @Test
  void givenMultipleTasksWithVariableNumberOfPrerequisites_thenSortInOrder()
      throws CircularDependencyDetectedException {
    final Set<String> taskTwoPrerequisites = new HashSet<>(Arrays.asList(TASK_3_NAME));
    final Set<String> taskThreePrerequisites = new HashSet<>(Arrays.asList(TASK_1_NAME));
    final Set<String> taskFourPrerequisites = new HashSet<>(
        Arrays.asList(TASK_2_NAME, TASK_3_NAME));

    final List<Task> tasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, taskTwoPrerequisites),
        new Task(TASK_3_NAME, TASK_COMMAND, taskThreePrerequisites),
        new Task(TASK_4_NAME, TASK_COMMAND, taskFourPrerequisites));

    final TasksGraph graph = new TasksGraph(tasks);

    final List<Task> expectedTasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_4_NAME, TASK_COMMAND, Collections.emptySet()));

    assertThat(graph.sort(), equalTo(expectedTasks));
  }

  @Test
  void givenMultipleUnsortedTasksWithTwoTasksWithTheSamePrerequisites_thenSortInOrder()
      throws CircularDependencyDetectedException {
    final Set<String> taskTwoPrerequisites = new HashSet<>(Arrays.asList(TASK_1_NAME));
    final Set<String> taskThreePrerequisites = new HashSet<>(Arrays.asList(TASK_1_NAME));
    final Set<String> taskFourPrerequisites = new HashSet<>(
        Arrays.asList(TASK_2_NAME, TASK_3_NAME));

    final List<Task> tasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, taskTwoPrerequisites),
        new Task(TASK_3_NAME, TASK_COMMAND, taskThreePrerequisites),
        new Task(TASK_4_NAME, TASK_COMMAND, taskFourPrerequisites));

    final TasksGraph graph = new TasksGraph(tasks);

    final List<Task> expectedTasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_4_NAME, TASK_COMMAND, Collections.emptySet()));

    assertThat(graph.sort(), equalTo(expectedTasks));
  }

  @Test
  void givenSevenTasksWithMultiplePrerequisites_thenSortInOrder()
      throws CircularDependencyDetectedException {
    final Set<String> taskTwoPrerequisites = new HashSet<>(Arrays.asList(TASK_1_NAME, TASK_7_NAME));
    final Set<String> taskThreePrerequisites = new HashSet<>(
        Arrays.asList(TASK_1_NAME, TASK_2_NAME));
    final Set<String> taskFourPrerequisites = new HashSet<>(
        Arrays.asList(TASK_6_NAME, TASK_3_NAME));
    final Set<String> taskFivePrerequisites = new HashSet<>(
        Arrays.asList(TASK_6_NAME));
    final Set<String> taskSixPrerequisites = new HashSet<>(
        Arrays.asList(TASK_7_NAME, TASK_2_NAME));

    final List<Task> tasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, taskTwoPrerequisites),
        new Task(TASK_3_NAME, TASK_COMMAND, taskThreePrerequisites),
        new Task(TASK_4_NAME, TASK_COMMAND, taskFourPrerequisites),
        new Task(TASK_5_NAME, TASK_COMMAND, taskFivePrerequisites),
        new Task(TASK_6_NAME, TASK_COMMAND, taskSixPrerequisites),
        new Task(TASK_7_NAME, TASK_COMMAND, Collections.emptySet()));

    final TasksGraph graph = new TasksGraph(tasks);

    final List<Task> expectedTasks = Arrays.asList(
        new Task(TASK_1_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_7_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_2_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_3_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_6_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_4_NAME, TASK_COMMAND, Collections.emptySet()),
        new Task(TASK_5_NAME, TASK_COMMAND, Collections.emptySet()));

    assertThat(graph.sort(), equalTo(expectedTasks));
  }
}
