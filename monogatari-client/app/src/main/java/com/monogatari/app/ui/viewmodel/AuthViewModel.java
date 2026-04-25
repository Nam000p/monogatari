package com.monogatari.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.model.auth.AuthResponse;
import com.monogatari.app.data.model.auth.LoginRequest;
import com.monogatari.app.data.model.auth.RegisterRequest;
import com.monogatari.app.data.model.auth.VerifyOtpRequest;
import com.monogatari.app.data.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<String>();
    private final MutableLiveData<AuthResponse> loginResponse = new MutableLiveData<AuthResponse>();
    private final MutableLiveData<String> registerResponse = new MutableLiveData<String>();
    private final MutableLiveData<String> verifyResponse = new MutableLiveData<String>();
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<AuthResponse> getLoginResponse() {
        return loginResponse;
    }
    public LiveData<String> getRegisterResponse() {
        return registerResponse;
    }
    public LiveData<String> getVerifyResponse() {
        return verifyResponse;
    }
    public void login(LoginRequest request) {
        isLoading.setValue(true);
        authRepository.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginResponse.setValue(response.body());
                } else {
                    errorMessage.setValue("Login failed: Invalid credentials");
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + throwable.getMessage());
            }
        });
    }
    public void register(RegisterRequest request) {
        isLoading.setValue(true);
        authRepository.register(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    registerResponse.setValue(response.body());
                } else {
                    errorMessage.setValue("Registration failed: Email or Username already exists");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    public void verifyOtp(VerifyOtpRequest request) {
        isLoading.setValue(true);
        authRepository.verifyAccount(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    verifyResponse.setValue(response.body());
                } else {
                    errorMessage.setValue("Verification failed: Invalid or expired OTP");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}