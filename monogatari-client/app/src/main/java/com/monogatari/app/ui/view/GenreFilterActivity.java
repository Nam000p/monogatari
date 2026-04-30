package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.model.common.PageResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.StoryRepository;
import com.monogatari.app.databinding.ActivityGenreFilterBinding;
import com.monogatari.app.ui.adapter.StoryAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreFilterActivity extends AppCompatActivity {

    private ActivityGenreFilterBinding binding;
    private StoryAdapter storyAdapter;
    private Long genreId;
    private String genreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenreFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        genreId = getIntent().getLongExtra("EXTRA_GENRE_ID", -1);
        genreName = getIntent().getStringExtra("EXTRA_GENRE_NAME");

        if (genreId == -1) {
            finish();
            return;
        }

        setupUI();
        fetchStoriesByGenre();
    }

    private void setupUI() {
        binding.tvGenreTitle.setText(genreName);

        storyAdapter = new StoryAdapter(story -> {
            Intent intent = new Intent(this, StoryDetailActivity.class);
            intent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.getId());
            startActivity(intent);
        });

        binding.rvStories.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvStories.setAdapter(storyAdapter);
    }

    private void fetchStoriesByGenre() {
        binding.progressBar.setVisibility(View.VISIBLE);

        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);

        StoryRepository storyRepository = new StoryRepository(storyApi);

        storyRepository.getStoriesByGenre(genreId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Response<PageResponse<StoryResponse>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null) {
                    storyAdapter.setStories(response.body().getContent());
                } else {
                    Toast.makeText(GenreFilterActivity.this, "No stories found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(GenreFilterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}