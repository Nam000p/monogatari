package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.model.auth.AuthResponse;
import com.monogatari.app.data.model.auth.ForgotPasswordRequest;
import com.monogatari.app.data.model.auth.LoginRequest;
import com.monogatari.app.data.model.auth.RegisterRequest;
import com.monogatari.app.data.model.auth.ResetPasswordRequest;
import com.monogatari.app.data.model.auth.VerifyOtpRequest;
import com.monogatari.app.data.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<AuthResponse> loginResponse = new MutableLiveData<>();

    private final MutableLiveData<String> registerResponse = new MutableLiveData<>();

    private final MutableLiveData<String> verifyResponse = new MutableLiveData<>();

    private final MutableLiveData<String> genericSuccess = new MutableLiveData<>();

    private final MutableLiveData<Boolean> birthdaySuccess = new MutableLiveData<>();

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

    public LiveData<String> getGenericSuccess() {
        return genericSuccess;
    }

    public LiveData<Boolean> getBirthdaySuccess() {
        return birthdaySuccess;
    }

    public void login(LoginRequest request) {
        isLoading.setValue(true);
        authRepository.login(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginResponse.setValue(response.body());
                } else {
                    errorMessage.setValue("Login failed: Invalid credentials");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request) {
        isLoading.setValue(true);
        authRepository.register(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    registerResponse.setValue(response.body());
                } else {
                    errorMessage.setValue("Registration failed: User already exists");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void verifyOtp(VerifyOtpRequest request) {
        isLoading.setValue(true);
        authRepository.verifyAccount(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    verifyResponse.setValue(response.body());
                } else {
                    errorMessage.setValue("Verification failed: Invalid OTP");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        isLoading.setValue(true);
        authRepository.forgotPassword(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    genericSuccess.setValue(response.body() != null ? response.body() : "OTP sent to your email");
                } else {
                    errorMessage.setValue("Error: Could not process request");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void resetPassword(ResetPasswordRequest request) {
        isLoading.setValue(true);
        authRepository.resetPassword(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    genericSuccess.setValue("Password updated successfully");
                } else {
                    errorMessage.setValue("Reset failed: Invalid OTP or session");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}