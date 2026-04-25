package com.monogatari.app.service;

import com.monogatari.app.entity.Subscription;

public interface SubscriptionService {
	void validatePremiumAccess(Long userId);

	void activatePremiumPlan(Long userId, String stripeCustomerId, String stripeSubscriptionId);

	void revokePremiumPlan(String stripeSubscriptionId);

	Subscription getSubscriptionByUser(Long userId);
}