package com.example.demo.common.aspect;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DataAuthorityApect {

    @Pointcut("@annotation(com.example.demo.common.aspect.annotion.DataAuthority)")
    public void authorityPointCut() {

    }

    @Around("authorityPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // todo
        log.info("数据确权,此次确权的key为:{}");
        Object result = point.proceed();
        // todo
        log.info("差分隐私");

        return result;
    }


}
