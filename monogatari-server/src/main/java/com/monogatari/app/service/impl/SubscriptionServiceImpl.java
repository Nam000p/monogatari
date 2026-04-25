package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.entity.Subscription;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.PlanType;
import com.monogatari.app.enums.SubscriptionStatus;
import com.monogatari.app.repository.SubscriptionRepository;
import com.monogatari.app.repository.UserRepository;
import com.monogatari.app.service.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
	private final UserRepository userRepository;

	private final SubscriptionRepository subscriptionRepository;

	@Override
	public void validatePremiumAccess(Long userId) {
		Subscription subscription = subscriptionRepository.findByUserId(userId)
             .orElseThrow(() -> new AccessDeniedException("PREMIUM Plan is required!"));
		if (subscription.getStatus() != SubscriptionStatus.ACTIVE ||
			subscription.getCurrentPeriodEnd() == null ||
			subscription.getCurrentPeriodEnd().isBefore(Instant.now())) {
			throw new AccessDeniedException("PREMIUM Plan is expired!");
		}
	}

	@Override
    @Transactional
    @TrackExecutionTime
    @RateLimited(maxRequests = 5, timeWindowMs = 60000)
    public void activatePremiumPlan(Long userId, String stripeCustomerId, String stripeSubscriptionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (stripeCustomerId != null && !stripeCustomerId.isBlank()) {
            user.setStripeCustomerId(stripeCustomerId);
            userRepository.save(user);
        }
        Subscription subscription = subscriptionRepository.findByUserId(user.getId())
                .orElse(Subscription.builder()
                        .user(user)
                        .planType(PlanType.PREMIUM)
                        .currentPeriodStart(Instant.now())
                        .build());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPlanType(PlanType.PREMIUM);
        subscription.setStripeSubscriptionId(stripeSubscriptionId);
        subscription.setCurrentPeriodStart(Instant.now());
        subscription.setCurrentPeriodEnd(ZonedDateTime.now(ZoneOffset.UTC).plusMonths(1).toInstant());
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void revokePremiumPlan(String stripeSubscriptionId) {
        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription record not found for ID: " + stripeSubscriptionId));
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setCurrentPeriodEnd(Instant.now());
        subscriptionRepository.save(subscription);
        log.info("Premium Plan revoked for User ID: {} due to subscription cancellation", subscription.getUser().getId());
    }

    @Override
    public Subscription getSubscriptionByUser(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("No subscription found for user ID: " + userId));
    }
}