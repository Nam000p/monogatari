package com.monogatari.app.ui.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.monogatari.app.databinding.ActivitySubscriptionBinding;

public class SubscriptionActivity extends AppCompatActivity {

    private ActivitySubscriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        binding.btnSubscribe.setOnClickListener(v -> {
            Toast.makeText(this, "Redirecting to payment gateway...", Toast.LENGTH_SHORT).show();
        });

        binding.btnCancelSubscription.setOnClickListener(v -> {
            Toast.makeText(this, "Subscription cancelled", Toast.LENGTH_SHORT).show();
        });
    }
}