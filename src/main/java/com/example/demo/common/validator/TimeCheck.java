package com.example.demo.common.validator;

import com.example.demo.common.validator.TimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = TimeValidator.class)
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface TimeCheck {

    String message() default "时间格式异常";

    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}