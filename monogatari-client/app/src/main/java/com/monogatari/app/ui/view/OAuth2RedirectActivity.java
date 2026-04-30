package com.monogatari.app.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.local.TokenManager;
import com.monogatari.app.data.model.user.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OAuth2RedirectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();

        if (uri != null) {
            handleRedirect(uri);
        } else {
            navigateToLogin("Invalid redirect data.");
        }
    }

    private void handleRedirect(Uri uri) {
        String token = uri.getQueryParameter("token");
        String refreshToken = uri.getQueryParameter("refreshToken");
        String error = uri.getQueryParameter("error");

        if (token != null && refreshToken != null) {
            TokenManager.getInstance(this).saveTokens(token, refreshToken);
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            checkProfileAndNavigate();
        } else if (error != null) {
            navigateToLogin("Authentication failed: " + error);
        } else {
            navigateToLogin("Tokens not found in redirect.");
        }
    }

    private void checkProfileAndNavigate() {
        UserApi userApi = ApiClient.getClient(this).create(UserApi.class);
        userApi.getMyProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String birthDate = response.body().getBirthDate();
                    if (birthDate == null) {
                        startActivity(new Intent(OAuth2RedirectActivity.this, BirthdayActivity.class));
                    } else {
                        startActivity(new Intent(OAuth2RedirectActivity.this, MainActivity.class));
                    }
                } else {
                    startActivity(new Intent(OAuth2RedirectActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                startActivity(new Intent(OAuth2RedirectActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void navigateToLogin(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}