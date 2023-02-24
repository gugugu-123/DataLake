package com.example.demo.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ModeValidator implements ConstraintValidator<Model, String> {

    private Model annotation;

    @Override
    public void initialize(Model constraintAnnotation) {
        this.annotation = constraintAnnotation;

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        boolean flag = false;

        Class<?> cls = annotation.target();
        if (cls.isEnum()) {
            Object[] objects = cls.getEnumConstants();
            for (Object obj : objects) {
                if (obj.toString().equals(value)) {
                    flag = true;
                    break;
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

}
