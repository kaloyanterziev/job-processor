package com.krterziev.jobprocessor.exceptions;

public class CircularDependencyDetectedException extends Exception {
  public CircularDependencyDetectedException() {
    super("Circular Dependency Detected");
  }
}
