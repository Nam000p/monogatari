package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.AuthApi;
import com.monogatari.app.data.model.auth.AuthResponse;
import com.monogatari.app.data.model.auth.ForgotPasswordRequest;
import com.monogatari.app.data.model.auth.LoginRequest;
import com.monogatari.app.data.model.auth.RegisterRequest;
import com.monogatari.app.data.model.auth.ResetPasswordRequest;
import com.monogatari.app.data.model.auth.VerifyOtpRequest;

import retrofit2.Call;

public class AuthRepository {
    private final AuthApi authApi;
    public AuthRepository(AuthApi authApi) {
        this.authApi = authApi;
    }
    public Call<String> register(RegisterRequest request) {
        return authApi.register(request);
    }
    public Call<AuthResponse> login(LoginRequest request) {
        return authApi.login(request);
    }
    public Call<String> verifyAccount(VerifyOtpRequest request) {
        return authApi.verifyAccount(request);
    }
    public Call<String> forgotPassword(ForgotPasswordRequest request) {
        return authApi.forgotPassword(request);
    }
    public Call<String> resetPassword(ResetPasswordRequest request) {
        return authApi.resetPassword(request);
    }
}