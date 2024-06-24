package com.project.api.validators.phoneorcellphone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PhoneOrCellphoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneOrCellphone {

  String message() default "must contain 10 or 11 digits";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
