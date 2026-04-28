package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AuthApi;
import com.monogatari.app.data.model.auth.ResetPasswordRequest;
import com.monogatari.app.data.repository.AuthRepository;
import com.monogatari.app.databinding.ActivityResetPasswordBinding;
import com.monogatari.app.ui.viewmodel.AuthViewModel;
import com.monogatari.app.ui.viewmodel.AuthViewModelFactory;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;

    private AuthViewModel authViewModel;

    private String email;

    private String otpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");
        otpCode = getIntent().getStringExtra("otp");

        initViewModel();
        setupObservers();
        setupListeners();
    }

    private void initViewModel() {
        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        AuthRepository repository = new AuthRepository(authApi);
        AuthViewModelFactory factory = new AuthViewModelFactory(repository);
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
    }

    private void setupListeners() {
        binding.btnUpdatePassword.setOnClickListener(v -> {
            String newPassword = binding.etNewPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail(email);
            request.setOtpCode(otpCode);
            request.setNewPassword(newPassword);

            authViewModel.resetPassword(request);
        });
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnUpdatePassword.setEnabled(!isLoading);
            binding.btnUpdatePassword.setText(isLoading ? "UPDATING..." : "RESET PASSWORD");
        });

        authViewModel.getGenericSuccess().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, "Password updated successfully! Please login.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}