package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AuthApi;
import com.monogatari.app.data.model.auth.VerifyOtpRequest;
import com.monogatari.app.data.repository.AuthRepository;
import com.monogatari.app.databinding.ActivityVerifyOtpBinding;
import com.monogatari.app.ui.viewmodel.AuthViewModel;
import com.monogatari.app.ui.viewmodel.AuthViewModelFactory;

public class VerifyOtpActivity extends AppCompatActivity {
    private ActivityVerifyOtpBinding binding;
    private AuthViewModel authViewModel;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");

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
        binding.btnVerify.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString().trim();
            if (otp.length() < 6) {
                Toast.makeText(this, "Please enter 6-digit code", Toast.LENGTH_SHORT).show();
                return;
            }
            VerifyOtpRequest request = new VerifyOtpRequest();
            request.setEmail(email);
            request.setOtpCode(otp);
            authViewModel.verifyOtp(request);
        });

        binding.tvResendOtp.setOnClickListener(v -> {
            // Optional: Call forgot-password or a resend-otp endpoint here
            Toast.makeText(this, "OTP resend triggered", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnVerify.setEnabled(!isLoading);
            binding.btnVerify.setText(isLoading ? "VERIFYING..." : "VERIFY ACCOUNT");
        });

        authViewModel.getVerifyResponse().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, "Account verified! You can login now.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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