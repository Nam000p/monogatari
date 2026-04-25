package com.monogatari.app.service;

import com.monogatari.app.annotation.LogIgnore;
import com.monogatari.app.entity.User;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
	private final StripeClient stripeClient;

	@Value("${app.payment.premium-price-id}")
	private String premiumPriceId;

	@Value("${app.payment.success-url}")
	private String successUrlBase;

	@Value("${app.payment.cancel-url}")
	private String cancelUrl;

	@LogIgnore
	public String createCheckoutSession(User user) throws StripeException {
		String successUrl = successUrlBase + "?session_id={CHECKOUT_SESSION_ID}";
		SessionCreateParams.Builder builder = SessionCreateParams.builder()
				.setMode(SessionCreateParams.Mode.SUBSCRIPTION)
				.setSuccessUrl(successUrl)
				.setCancelUrl(cancelUrl)
				.setClientReferenceId(user.getId().toString());
		if (user.getStripeCustomerId() != null && !user.getStripeCustomerId().isBlank()) {
			builder.setCustomer(user.getStripeCustomerId());
		} else {
			builder.setCustomerEmail(user.getEmail());
		}
		builder.addLineItem(SessionCreateParams.LineItem.builder()
				.setQuantity(1L)
				.setPrice(premiumPriceId)
				.build());
		try {
			Session session = stripeClient.checkout().sessions().create(builder.build());
			return session.getUrl();
		} catch (StripeException e) {
			log.error("STRIPE_API_FAILURE: Request ID: {}, Error: {}", e.getRequestId(), e.getMessage());
			throw e;
		}
	}

	public void cancelSubscription(String subscriptionId) throws StripeException {
		try {
			com.stripe.param.SubscriptionUpdateParams params = com.stripe.param.SubscriptionUpdateParams.builder()
					.setCancelAtPeriodEnd(true)
					.build();
			stripeClient.subscriptions().update(subscriptionId, params);
			log.info("Subscription marked for cancellation at period end: {}", subscriptionId);
		} catch (StripeException e) {
			log.error("Failed to cancel subscription {}: {}", subscriptionId, e.getMessage());
			throw e;
		}
	}
}