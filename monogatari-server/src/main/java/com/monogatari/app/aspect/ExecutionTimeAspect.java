package com.monogatari.app.aspect;

import com.monogatari.app.annotation.TrackExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {
    @Around("@annotation(trackExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, TrackExecutionTime trackExecutionTime) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        String methodName = joinPoint.getSignature().toShortString();
        String customDescription = trackExecutionTime.value();
        stopWatch.start();
        try {
            return  joinPoint.proceed();
        } finally {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            if (totalTimeMillis > 500) {
                log.warn("PERFORMANCE ALERT - METHOD [{}] {} executed in {} ms", methodName, customDescription, totalTimeMillis);
            } else {
                log.info("Method [{}] {} executed in {} ms", methodName, customDescription, totalTimeMillis);
            }
        }
    }
}