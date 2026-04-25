package com.monogatari.app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
	@Pointcut("within(com.monogatari.app.service..*) && !@annotation(com.monogatari.app.annotation.LogIgnore)")
	public void secureServiceMethods() {}

	@Before("secureServiceMethods()")
	public void logBefore(JoinPoint joinPoint) {
		log.debug("==> Entering: [{}]. Arguments count: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getArgs().length);
	}

	@AfterReturning(pointcut = "secureServiceMethods()", returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		log.debug("<== Exiting: [{}] with result type: {}",
                joinPoint.getSignature().getName(),
                (result != null ? result.getClass().getSimpleName() : "void"));
	}

	@AfterThrowing(pointcut = "secureServiceMethods()", throwing = "exception")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
		log.error("!!! Exception in [{}]: {}",
                joinPoint.getSignature().getName(),
                exception.getMessage());
	}
}