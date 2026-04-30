package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.monogatari.app.R;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AuthorApi;
import com.monogatari.app.data.model.author.AuthorResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.AuthorRepository;
import com.monogatari.app.databinding.ActivityAuthorBinding;
import com.monogatari.app.ui.adapter.StoryAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorActivity extends AppCompatActivity {
    private ActivityAuthorBinding binding;
    private Long authorId;
    private StoryAdapter storyAdapter;
    private AuthorRepository authorRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authorId = getIntent().getLongExtra("EXTRA_AUTHOR_ID", -1);
        if (authorId == -1) {
            Toast.makeText(this, "Author ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AuthorApi api = ApiClient.getClient(this).create(AuthorApi.class);
        authorRepository = new AuthorRepository(api);

        setupUI();
        fetchAuthorData();
    }

    private void setupUI() {
        storyAdapter = new StoryAdapter(story -> {
            Intent intent = new Intent(this, StoryDetailActivity.class);
            intent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.getId());
            startActivity(intent);
        });

        binding.rvAuthorStories.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvAuthorStories.setAdapter(storyAdapter);
    }

    private void fetchAuthorData() {
        fetchAuthorProfile();
        fetchAuthorStories();
    }

    private void fetchAuthorProfile() {
        authorRepository.getAuthorDetails(authorId).enqueue(new Callback<AuthorResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthorResponse> call, @NonNull Response<AuthorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindAuthorProfile(response.body());
                } else {
                    Toast.makeText(AuthorActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthorResponse> call, @NonNull Throwable t) {
                Toast.makeText(AuthorActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAuthorStories() {
        authorRepository.getStoriesByAuthor(authorId).enqueue(new Callback<List<StoryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<StoryResponse>> call, @NonNull Response<List<StoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    storyAdapter.setStories(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StoryResponse>> call, @NonNull Throwable t) {
                Toast.makeText(AuthorActivity.this, "Failed to load stories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindAuthorProfile(AuthorResponse author) {
        binding.tvAuthorName.setText(author.getName() != null ? author.getName() : "Unknown Author");
        binding.tvAuthorBio.setText(author.getBio() != null ? author.getBio() : "No biography available.");

        if (author.getAvatarUrl() != null && !author.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(author.getAvatarUrl())
                    .placeholder(R.drawable.default_avatar)
                    .circleCrop()
                    .into(binding.ivAuthorAvatar);
        } else {
            binding.ivAuthorAvatar.setImageResource(R.drawable.default_avatar);
        }
    }
}