package com.krterziev.jobprocessor.services;

import com.krterziev.jobprocessor.models.Task;
import com.krterziev.jobprocessor.models.TasksGraph;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService{

  @Override
  public List<Task> sortTasks(final List<Task> tasks) {
    final TasksGraph tasksGraph = new TasksGraph(tasks);
    return tasksGraph.sort();
  }
}
