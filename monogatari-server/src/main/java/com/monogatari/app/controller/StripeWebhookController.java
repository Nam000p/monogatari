package com.monogatari.app.controller;

import com.monogatari.app.service.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/webhook")
public class StripeWebhookController {
    private final StripeWebhookService stripeWebhookService;

	@PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            stripeWebhookService.handleStripeWebhook(payload, sigHeader);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Webhook Error: {}", e.getMessage());
            return new ResponseEntity<>("Webhook failure", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}