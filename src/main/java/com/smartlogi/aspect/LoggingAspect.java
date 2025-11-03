package com.smartlogi.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.smartlogi.service..*(..))")
    public void serviceLayerExecution() {}

    @Around("serviceLayerExecution()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        logger.info("Enter: {}.{}() with argument[s] = {}", className, methodName, joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.info("Exit: {}.{}() executed in {}ms, result = {}", className, methodName, executionTime, result);

            return result;

        } catch (Exception e) {
            logger.error("Exception in {}.{}() with cause = {}", className, methodName, e.getCause() != null ? e.getCause() : "NULL");
            throw e;
        }
    }
}