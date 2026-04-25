package com.monogatari.app.data.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;

public interface PaymentApi {
    @POST("payments/create-checkout-session")
    Call<Map<String, String>> createCheckoutSession();
}