package com.monogatari.app.controller;

import com.monogatari.app.dto.payment.PaymentResponse;
import com.monogatari.app.entity.Subscription;
import com.monogatari.app.entity.User;
import com.monogatari.app.service.PaymentService;
import com.monogatari.app.service.SubscriptionService;
import com.monogatari.app.service.UserService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	private final UserService userService;

	private final SubscriptionService subscriptionService;

	@PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createSession() {
       try {
          User user = userService.getCurrentAuthenticateUser();
          String checkoutUrl = paymentService.createCheckoutSession(user);
          return new ResponseEntity<>(Map.of("url", checkoutUrl), HttpStatus.OK);
       } catch (Exception e) {
          log.error("SESSION_CREATION_ERROR: {}", e.getMessage());
          return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

	@PutMapping("/cancel-subscription")
    public ResponseEntity<PaymentResponse> cancelSubscription() {
       try {
          User user = userService.getCurrentAuthenticateUser();
          if (user == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .body(new PaymentResponse(false, "User authentication failed"));
          }
          Subscription subscription = subscriptionService.getSubscriptionByUser(user.getId());
          if (subscription == null || subscription.getStripeSubscriptionId() == null) {
             return ResponseEntity.badRequest()
                   .body(new PaymentResponse(false, "No active stripe subscription found"));
          }
          paymentService.cancelSubscription(subscription.getStripeSubscriptionId());
          return ResponseEntity.ok(new PaymentResponse(true, "Subscription will be cancelled at the end of the period"));
       } catch (StripeException e) {
          log.error("STRIPE_API_ERROR: {}", e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PaymentResponse(false, "Stripe error: " + e.getMessage()));
       } catch (Exception e) {
          log.error("INTERNAL_SERVER_ERROR: ", e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PaymentResponse(false, "System error: " + e.getMessage()));
       }
    }

    @GetMapping("/success")
    public ResponseEntity<Void> paymentSuccess() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("monogatari://payment-success"))
                .build();
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> paymentCancel() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("monogatari://payment-cancel"))
                .build();
    }
}