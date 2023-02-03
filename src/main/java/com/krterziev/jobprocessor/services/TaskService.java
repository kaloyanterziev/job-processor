package com.krterziev.jobprocessor.services;

import com.krterziev.jobprocessor.models.Task;
import java.util.List;

public interface TaskService {
  List<Task> sortTasks(List<Task> tasks);
}
