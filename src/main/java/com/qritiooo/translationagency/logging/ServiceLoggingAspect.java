package com.qritiooo.translationagency.logging;

import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("execution(public * com.qritiooo.translationagency.service.impl..*(..))")
    public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName()
                + "."
                + signature.getName();

        if (log.isDebugEnabled()) {
            log.debug(
                    "Execution of method: {} with arguments: {}",
                    methodName,
                    Arrays.toString(joinPoint.getArgs())
            );
        }

        try {
            Object result = joinPoint.proceed();
            log.debug(
                    "Method {} completed in {} ms",
                    methodName,
                    System.currentTimeMillis() - start
            );
            return result;
        } catch (Exception exception) {
            log.error(
                    "Method {} failed in {} ms",
                    methodName,
                    System.currentTimeMillis() - start
            );
            throw exception;
        }
    }
}
