package com.krterziev.jobprocessor.validators;

import com.krterziev.jobprocessor.payload.request.TaskRequest;
import com.krterziev.jobprocessor.payload.request.TasksRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskPrerequisitesValidator implements
    ConstraintValidator<TaskPrerequisitesConstraint, TasksRequest> {


  @Override
  public boolean isValid(TasksRequest value, ConstraintValidatorContext context) {
    final Set<String> tasksNames = value.tasks().stream().map(TaskRequest::name).collect(
        Collectors.toSet());
    for (TaskRequest taskRequest : value.tasks()) {
      if (taskRequest.requires() != null && !tasksNames.containsAll(taskRequest.requires())) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String
                .format("Some of the prerequisites of task %s does not exist.", taskRequest.name()))
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }
}
