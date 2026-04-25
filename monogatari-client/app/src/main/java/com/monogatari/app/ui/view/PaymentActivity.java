package com.monogatari.app.ui.view;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;
import com.monogatari.app.data.api.*;
import com.monogatari.app.data.repository.*;
import com.monogatari.app.databinding.ActivityPaymentBinding;
import com.monogatari.app.ui.viewmodel.StoryViewModel;
import com.monogatari.app.ui.viewmodel.StoryViewModelFactory;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private StoryViewModel storyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        setupObservers();

        // Single payment button - No planId needed
        binding.btnPayMonthly.setOnClickListener(v -> storyViewModel.createPayment());
    }

    private void initViewModel() {
        // Init all required APIs
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        ChapterApi chapterApi = ApiClient.getClient(this).create(ChapterApi.class);
        CommentApi commentApi = ApiClient.getClient(this).create(CommentApi.class);
        RatingApi ratingApi = ApiClient.getClient(this).create(RatingApi.class);
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        GenreApi genreApi = ApiClient.getClient(this).create(GenreApi.class);
        AuthorApi authorApi = ApiClient.getClient(this).create(AuthorApi.class);
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        PaymentApi paymentApi = ApiClient.getClient(this).create(PaymentApi.class);

        // Factory with 9 repositories
        StoryViewModelFactory factory = new StoryViewModelFactory(
                new StoryRepository(storyApi), new ChapterRepository(chapterApi),
                new CommentRepository(commentApi), new RatingRepository(ratingApi),
                new ReadingProgressRepository(progressApi), new GenreRepository(genreApi),
                new AuthorRepository(authorApi), new AiRepository(aiApi),
                new PaymentRepository(paymentApi)
        );

        storyViewModel = new ViewModelProvider(this, factory).get(StoryViewModel.class);
    }

    private void setupObservers() {
        // Observe the Stripe URL from ViewModel
        storyViewModel.getPaymentUrl().observe(this, url -> {
            if (url != null && !url.isEmpty()) {
                // Open Stripe Checkout in Chrome Custom Tabs
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));
            }
        });

        // Observe loading state to show/hide progress (optional)
        storyViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnPayMonthly.setEnabled(!isLoading);
            if (isLoading) {
                Toast.makeText(this, "Opening Secure Checkout...", Toast.LENGTH_SHORT).show();
            }
        });

        // Error handling
        storyViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}