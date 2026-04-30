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

    private String flow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");
        flow = getIntent().getStringExtra("flow");

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
            String otp = binding.etOtpCode.getText().toString().trim();
            if (otp.length() < 6) {
                Toast.makeText(this, "Please enter 6-digit code", Toast.LENGTH_SHORT).show();
                return;
            }

            VerifyOtpRequest request = new VerifyOtpRequest();
            request.setEmail(email);
            request.setOtpCode(otp);
            authViewModel.verifyOtp(request);
        });
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnVerify.setEnabled(!isLoading);
            binding.btnVerify.setText(isLoading ? "VERIFYING..." : "VERIFY ACCOUNT");
        });

        authViewModel.getVerifyResponse().observe(this, message -> {
            if (message != null) {
                handleNavigationAfterVerification();
            }
        });

        authViewModel.getGenericSuccess().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, "A new code has been sent to " + email, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleNavigationAfterVerification() {
        Intent intent;
        if ("recovery".equals(flow)) {
            intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("otp", binding.etOtpCode.getText().toString().trim());
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}