package com.monogatari.app.service;

import com.monogatari.app.entity.Transaction;
import com.monogatari.app.enums.CurrencyType;
import com.monogatari.app.enums.TransactionStatus;
import com.monogatari.app.repository.TransactionRepository;
import com.monogatari.app.repository.UserRepository;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookService {
    private final SubscriptionService subscriptionService;

	private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

	@Value("${stripe.webhook.secret}")
	private String endpointSecret;

    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            log.info("Webhook received: {}", event.getType());
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) deserializer.deserializeUnsafe();
                handleCheckoutSession(session);
            }
            else if ("customer.subscription.deleted".equals(event.getType())) {
                com.stripe.model.Subscription subscription = (com.stripe.model.Subscription) deserializer.deserializeUnsafe();
                subscriptionService.revokePremiumPlan(subscription.getId());
            }
            else if ("customer.subscription.updated".equals(event.getType())) {
                com.stripe.model.Subscription subscription = (com.stripe.model.Subscription) deserializer.deserializeUnsafe();
                if (subscription.getCancelAtPeriodEnd()) {
                    log.info("Subscription set to cancel at period end: {}", subscription.getId());
                }
            }
        } catch (Exception e) {
            log.error("Webhook Error: {}", e.getMessage());
        }
    }

    private void handleCheckoutSession(Session session) {
        String userIdStr = session.getClientReferenceId();
        String stripeCustomerId = session.getCustomer();
        String subscriptionId = session.getSubscription();
        BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100));
        if (userIdStr != null) {
            Long userId = Long.parseLong(userIdStr);
            subscriptionService.activatePremiumPlan(userId, stripeCustomerId, subscriptionId);
            userRepository.findById(userId).ifPresent(user -> {
                Transaction transaction = Transaction.builder()
                        .user(user)
                        .stripeInvoiceId(subscriptionId)
                        .amount(amount)
                        .currency(CurrencyType.USD)
                        .status(TransactionStatus.PAID)
                        .build();
                transactionRepository.save(transaction);
            });
        }
    }
}