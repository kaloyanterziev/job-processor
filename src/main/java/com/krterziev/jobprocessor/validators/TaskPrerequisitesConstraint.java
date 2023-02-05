package com.krterziev.jobprocessor.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TaskPrerequisitesValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskPrerequisitesConstraint {

  String message() default "Invalid task prerequisites";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
