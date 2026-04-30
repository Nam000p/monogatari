package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.repository.StoryRepository;
import com.monogatari.app.databinding.ActivityExploreBinding;
import com.monogatari.app.ui.adapter.StoryAdapter;
import com.monogatari.app.ui.viewmodel.ExploreViewModel;
import com.monogatari.app.ui.viewmodel.ExploreViewModelFactory;

public class ExploreActivity extends AppCompatActivity {
    private ActivityExploreBinding binding;
    private ExploreViewModel viewModel;
    private StoryAdapter adapter;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDependencies();
        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void initDependencies() {
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        StoryRepository repository = new StoryRepository(storyApi);
        ExploreViewModelFactory factory = new ExploreViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(ExploreViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new StoryAdapter(story -> {
            Intent intent = new Intent(this, StoryDetailActivity.class);
            intent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.getId());
            startActivity(intent);
        });
        binding.rvSearchResults.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvSearchResults.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (query.length() >= 2) {
                        viewModel.searchStories(query);
                    } else if (query.isEmpty()) {
                        adapter.setStories(null);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });

        binding.btnClear.setOnClickListener(v -> {
            binding.etSearchQuery.setText("");
            adapter.setStories(null);
        });
    }

    private void setupObservers() {
        viewModel.getSearchResults().observe(this, stories -> {
            if (stories == null || stories.isEmpty()) {
                adapter.setStories(null);
                binding.tvNoResults.setVisibility(View.VISIBLE);
            } else {
                adapter.setStories(stories);
                binding.tvNoResults.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);

            if (isLoading) {
                binding.tvNoResults.setVisibility(View.GONE);
            }
            binding.etSearchQuery.setEnabled(!isLoading);
        });
    }
}