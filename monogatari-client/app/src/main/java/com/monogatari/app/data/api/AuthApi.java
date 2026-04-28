package com.monogatari.app.data.api;

import com.monogatari.app.data.model.auth.AuthResponse;
import com.monogatari.app.data.model.auth.ForgotPasswordRequest;
import com.monogatari.app.data.model.auth.LoginRequest;
import com.monogatari.app.data.model.auth.RegisterRequest;
import com.monogatari.app.data.model.auth.ResetPasswordRequest;
import com.monogatari.app.data.model.auth.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/register")
    Call<String> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/logout")
    Call<String> logout();

    @POST("auth/refresh")
    Call<AuthResponse> refreshToken();

    @POST("auth/verify-account")
    Call<String> verifyAccount(@Body VerifyOtpRequest request);

    @POST("auth/forgot-password")
    Call<String> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/reset-password")
    Call<String> resetPassword(@Body ResetPasswordRequest request);
}