package com.monogatari.app.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monogatari.app.data.api.*;
import com.monogatari.app.data.repository.*;
import com.monogatari.app.databinding.ActivityMainBinding;
import com.monogatari.app.ui.adapter.StoryAdapter;
import com.monogatari.app.ui.viewmodel.StoryViewModel;
import com.monogatari.app.ui.viewmodel.StoryViewModelFactory;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private StoryViewModel storyViewModel;
    private StoryAdapter storyAdapter;
    private String currentSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handleDeepLink();
        initRecyclerView();
        initViewModel();
        setupObservers();
        setupSearch();
        setupPagination();
        setupProfileNavigation();
        setupAiNavigation();

        storyViewModel.loadStories(null, true);
    }

    private void handleDeepLink() {
        Uri data = getIntent().getData();
        if (data != null && data.getHost().equals("app.com")) {
            if (data.getPath().contains("success")) {
                Toast.makeText(this, "Welcome to Premium, Sir!", Toast.LENGTH_LONG).show();
            } else if (data.getPath().contains("cancel")) {
                Toast.makeText(this, "Payment cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupAiNavigation() {
        binding.fabAiChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AiChatActivity.class);
            startActivity(intent);
        });
    }

    private void setupPagination() {
        binding.rvStories.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == storyAdapter.getItemCount() - 1) {
                    storyViewModel.loadStories(currentSearch, false);
                }
            }
        });
    }

    private void setupProfileNavigation() {
        binding.ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void initRecyclerView() {
        storyAdapter = new StoryAdapter(new ArrayList<>());
        binding.rvStories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStories.setAdapter(storyAdapter);

        storyAdapter.setOnStoryClickListener(story -> {
            Intent intent = new Intent(MainActivity.this, StoryDetailActivity.class);
            intent.putExtra("story_data", story);
            startActivity(intent);
        });
    }

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearch = query;
                storyViewModel.loadStories(query, true);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    currentSearch = null;
                    storyViewModel.loadStories(null, true);
                }
                return true;
            }
        });
    }

    private void initViewModel() {
        // Initialize all 9 APIs (Added AI & Payment)
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        ChapterApi chapterApi = ApiClient.getClient(this).create(ChapterApi.class);
        CommentApi commentApi = ApiClient.getClient(this).create(CommentApi.class);
        RatingApi ratingApi = ApiClient.getClient(this).create(RatingApi.class);
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        GenreApi genreApi = ApiClient.getClient(this).create(GenreApi.class);
        AuthorApi authorApi = ApiClient.getClient(this).create(AuthorApi.class);
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        PaymentApi paymentApi = ApiClient.getClient(this).create(PaymentApi.class);

        // Initialize all 9 Repositories
        StoryRepository storyRepo = new StoryRepository(storyApi);
        ChapterRepository chapterRepo = new ChapterRepository(chapterApi);
        CommentRepository commentRepo = new CommentRepository(commentApi);
        RatingRepository ratingRepo = new RatingRepository(ratingApi);
        ReadingProgressRepository progressRepo = new ReadingProgressRepository(progressApi);
        GenreRepository genreRepo = new GenreRepository(genreApi);
        AuthorRepository authorRepo = new AuthorRepository(authorApi);
        AiRepository aiRepo = new AiRepository(aiApi);
        PaymentRepository paymentRepo = new PaymentRepository(paymentApi);

        StoryViewModelFactory factory = new StoryViewModelFactory(
                storyRepo, chapterRepo, commentRepo, ratingRepo, progressRepo, genreRepo, authorRepo, aiRepo, paymentRepo);

        storyViewModel = new ViewModelProvider(this, factory).get(StoryViewModel.class);
    }

    private void setupObservers() {
        storyViewModel.getStories().observe(this, stories -> {
            if (stories != null) storyAdapter.setStories(stories);
        });

        storyViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }
}