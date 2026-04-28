package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AuthApi;
import com.monogatari.app.data.model.auth.ForgotPasswordRequest;
import com.monogatari.app.data.repository.AuthRepository;
import com.monogatari.app.databinding.ActivityEmailInputBinding;
import com.monogatari.app.ui.viewmodel.AuthViewModel;
import com.monogatari.app.ui.viewmodel.AuthViewModelFactory;

public class EmailInputActivity extends AppCompatActivity {
    private ActivityEmailInputBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnSendOtp.setEnabled(!isLoading);
            binding.btnSendOtp.setText(isLoading ? "SENDING..." : "SEND CODE");
        });

        authViewModel.getGenericSuccess().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, VerifyOtpActivity.class);
                intent.putExtra("email", binding.etEmail.getText().toString().trim());
                intent.putExtra("flow", "recovery");
                startActivity(intent);
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        binding.btnSendOtp.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail(email);
            authViewModel.forgotPassword(request);
        });
    }
}