package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.PaymentApi;
import com.monogatari.app.data.model.payment.PaymentResponse;

import java.util.Map;

import retrofit2.Call;

public class PaymentRepository {
    private final PaymentApi paymentApi;

    public PaymentRepository(PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    public Call<Map<String, String>> createCheckout() {
        return paymentApi.createCheckoutSession();
    }

    public Call<PaymentResponse> cancelSubscription() {
        return paymentApi.cancelSubscription();
    }
}