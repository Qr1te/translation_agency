package com.qritiooo.translationagency.aspect;

import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.ConflictException;
import com.qritiooo.translationagency.exception.NotFoundException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
        } catch (Throwable exception) {
            long duration = System.currentTimeMillis() - start;
            HttpStatus handledStatus = resolveHandledStatus(exception);
            if (handledStatus != null) {
                log.warn(
                        "Method {} failed with {} error in {} ms: {}",
                        methodName,
                        handledStatus.value(),
                        duration,
                        exception.getMessage()
                );
            } else {
                log.error(
                        "Method {} failed in {} ms: {}",
                        methodName,
                        duration,
                        exception.getMessage(),
                        exception
                );
            }
            throw exception;
        }
    }

    private HttpStatus resolveHandledStatus(Throwable exception) {
        if (exception instanceof BadRequestException
                || exception instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (exception instanceof NotFoundException
                || exception instanceof NoSuchElementException) {
            return HttpStatus.NOT_FOUND;
        }
        if (exception instanceof ConflictException) {
            return HttpStatus.CONFLICT;
        }
        return null;
    }
}
