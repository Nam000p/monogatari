package com.monogatari.app.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.monogatari.app.data.local.TokenManager;

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

            Toast.makeText(this, "Login successful! Welcome to Monogatari.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (error != null) {
            navigateToLogin("Authentication failed: " + error);
        } else {
            navigateToLogin("Tokens not found in redirect.");
        }
    }

    private void navigateToLogin(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}