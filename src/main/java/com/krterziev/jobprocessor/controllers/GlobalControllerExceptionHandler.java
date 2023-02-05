package com.krterziev.jobprocessor.controllers;

import com.krterziev.jobprocessor.exceptions.CircularDependencyDetectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Circular Dependency Found")
  @ExceptionHandler(CircularDependencyDetectedException.class)
  public void CircularDependencyDetectedException(final CircularDependencyDetectedException ex) {
    LOG.warn(ex.getMessage());
  }
}
