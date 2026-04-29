package com.monogatari.app.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.PaymentApi;
import com.monogatari.app.data.repository.PaymentRepository;
import com.monogatari.app.databinding.ActivitySubscriptionBinding;
import com.monogatari.app.ui.viewmodel.SubscriptionViewModel;
import com.monogatari.app.ui.viewmodel.SubscriptionViewModelFactory;

public class SubscriptionActivity extends AppCompatActivity {
    private ActivitySubscriptionBinding binding;
    private SubscriptionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PaymentApi api = ApiClient.getClient(this).create(PaymentApi.class);
        PaymentRepository repository = new PaymentRepository(api);
        viewModel = new ViewModelProvider(this, new SubscriptionViewModelFactory(repository)).get(SubscriptionViewModel.class);

        setupListeners();
        observeViewModel();

        handleDeepLink(getIntent());
    }

    private void setupListeners() {
        binding.toolbarSubscription.setNavigationOnClickListener(v -> finish());

        binding.btnSubscribe.setOnClickListener(v -> viewModel.createCheckoutSession());

        binding.btnCancelSubscription.setOnClickListener(v -> viewModel.cancelSubscription());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnSubscribe.setEnabled(!isLoading);
            binding.btnCancelSubscription.setEnabled(!isLoading);
        });

        viewModel.getCheckoutUrl().observe(this, url -> {
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        viewModel.getMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        viewModel.getError().observe(this, err -> {
            if (err != null) Toast.makeText(this, "Error: " + err, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        if (intent == null) return;
        Uri data = intent.getData();
        if (data != null && "monogatari".equals(data.getScheme())) {
            if (data.toString().contains("payment-success")) {
                Toast.makeText(this, "WELCOME TO PREMIUM! 👑", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } else if (data.toString().contains("payment-cancel")) {
                Toast.makeText(this, "Payment cancelled. Try again!", Toast.LENGTH_SHORT).show();
            }
            intent.setData(null);
        }
    }
}