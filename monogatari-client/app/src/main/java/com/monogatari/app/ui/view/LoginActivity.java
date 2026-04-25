package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AuthApi;
import com.monogatari.app.data.local.TokenManager;
import com.monogatari.app.data.model.auth.LoginRequest;
import com.monogatari.app.data.repository.AuthRepository;
import com.monogatari.app.databinding.ActivityLoginBinding;
import com.monogatari.app.ui.viewmodel.AuthViewModel;
import com.monogatari.app.ui.viewmodel.AuthViewModelFactory;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = TokenManager.getInstance(this);
        if (tokenManager.getToken() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDependencies();
        setupObservers();
        setupListeners();
    }

    private void initDependencies() {
        tokenManager = TokenManager.getInstance(this);
        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        AuthRepository repository = new AuthRepository(authApi);
        AuthViewModelFactory factory = new AuthViewModelFactory(repository);
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            LoginRequest request = new LoginRequest();
            request.setEmail(email);
            request.setPassword(password);
            authViewModel.login(request);
        });

        binding.tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLogin.setText(isLoading ? "SIGNING IN..." : "SIGN IN");
        });

        authViewModel.getLoginResponse().observe(this, response -> {
            if (response != null && response.getToken() != null) {
                tokenManager.saveToken(response.getToken());
                Toast.makeText(this, "Welcome to Monogatari!", Toast.LENGTH_SHORT).show();

                // Intent to Home Screen later
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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