package com.monogatari.app.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.monogatari.app.data.api.*;
import com.monogatari.app.data.repository.*;
import com.monogatari.app.databinding.ActivityProfileBinding;
import com.monogatari.app.ui.viewmodel.*;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private UserViewModel userViewModel;
    private StoryViewModel storyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarProfile);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViewModels();
        setupObservers();

        userViewModel.loadProfile();

        binding.btnGoPremium.setOnClickListener(v -> storyViewModel.createPayment());

        binding.btnLogout.setOnClickListener(v -> {
            // Implementation for token removal goes here
            finish();
        });
    }

    private void initViewModels() {
        // User ViewModel Init
        UserApi userApi = ApiClient.getClient(this).create(UserApi.class);
        UserRepository userRepo = new UserRepository(userApi);
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepo)).get(UserViewModel.class);

        // Story ViewModel Init (Required for Payment)
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        ChapterApi chapterApi = ApiClient.getClient(this).create(ChapterApi.class);
        CommentApi commentApi = ApiClient.getClient(this).create(CommentApi.class);
        RatingApi ratingApi = ApiClient.getClient(this).create(RatingApi.class);
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        GenreApi genreApi = ApiClient.getClient(this).create(GenreApi.class);
        AuthorApi authorApi = ApiClient.getClient(this).create(AuthorApi.class);
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        PaymentApi paymentApi = ApiClient.getClient(this).create(PaymentApi.class);

        StoryViewModelFactory storyFactory = new StoryViewModelFactory(
                new StoryRepository(storyApi), new ChapterRepository(chapterApi),
                new CommentRepository(commentApi), new RatingRepository(ratingApi),
                new ReadingProgressRepository(progressApi), new GenreRepository(genreApi),
                new AuthorRepository(authorApi), new AiRepository(aiApi),
                new PaymentRepository(paymentApi)
        );
        storyViewModel = new ViewModelProvider(this, storyFactory).get(StoryViewModel.class);
    }

    private void setupObservers() {
        userViewModel.getUserProfile().observe(this, profile -> {
            if (profile != null) {
                binding.tvUsername.setText(profile.getUsername());
                binding.tvEmail.setText(profile.getEmail());
                Glide.with(this).load(profile.getAvatarUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery).into(binding.ivAvatar);
            }
        });

        // STRIPE URL OBSERVER: Launches Chrome/Browser
        storyViewModel.getPaymentUrl().observe(this, url -> {
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        storyViewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}