package com.example.demo.config;

import cn.hutool.core.util.StrUtil;
import com.example.demo.common.validate.TimeCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TimeValidator implements ConstraintValidator<TimeCheck, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isBlankIfStr(value)) {
            return true;
        }
        boolean flag = true;
        try {
            LocalDateTime time = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println(time.toString());
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

}
