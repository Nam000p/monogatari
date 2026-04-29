package com.monogatari.app.data.api;

import com.monogatari.app.data.model.payment.PaymentResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface PaymentApi {
    @POST("payments/create-checkout-session")
    Call<Map<String, String>> createCheckoutSession();

    @PUT("payments/cancel-subscription")
    Call<PaymentResponse> cancelSubscription();
}