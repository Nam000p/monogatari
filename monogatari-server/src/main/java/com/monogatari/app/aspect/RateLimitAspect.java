package com.monogatari.app.aspect;

import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.exception.ApiRateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {
	private final Map<String, UserRequestData> requestCounts = new ConcurrentHashMap<>();
	
	@Before("@annotation(rateLimited)")
	public void checkRateLimit(JoinPoint joinPoint, RateLimited rateLimited) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return;
		}
		HttpServletRequest request = attributes.getRequest();
		String clientIp = request.getRemoteAddr();
		String key = clientIp + "-" + joinPoint.getSignature().toShortString();
		long currentTime = System.currentTimeMillis();
		requestCounts.compute(key, (k, data) -> {
	        if (data == null || (currentTime - data.startTime) > rateLimited.timeWindowMs()) {
	            return new UserRequestData(currentTime, 1);
	        }
	        if (data.count >= rateLimited.maxRequests()) {
	            throw new ApiRateLimitException("Rate limit exceeded. Max: " + rateLimited.maxRequests());
	        }
	        data.count++;
	        return data;
	    });
	}
	
	private static class UserRequestData {
		long startTime;
		int count;
		
		public UserRequestData(long startTime, int count) {
			this.startTime = startTime;
			this.count = count;
		}
	}
}