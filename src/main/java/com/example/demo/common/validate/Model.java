package com.example.demo.common.validate;

import com.example.demo.common.commonEnum.ModelEnum;
import com.example.demo.config.ModeValidator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ModeValidator.class)
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface Model {
 
    String message() default "mode异常";

    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    /**
     * 目标枚举类
     */
    //Class<ModelEnum> target();
}