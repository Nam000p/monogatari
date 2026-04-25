package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.monogatari.app.data.api.AiApi;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AuthorApi;
import com.monogatari.app.data.api.ChapterApi;
import com.monogatari.app.data.api.CommentApi;
import com.monogatari.app.data.api.FollowApi;
import com.monogatari.app.data.api.GenreApi;
import com.monogatari.app.data.api.PaymentApi;
import com.monogatari.app.data.api.RatingApi;
import com.monogatari.app.data.api.ReadingProgressApi;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.AiRepository;
import com.monogatari.app.data.repository.AuthorRepository;
import com.monogatari.app.data.repository.ChapterRepository;
import com.monogatari.app.data.repository.CommentRepository;
import com.monogatari.app.data.repository.GenreRepository;
import com.monogatari.app.data.repository.PaymentRepository;
import com.monogatari.app.data.repository.RatingRepository;
import com.monogatari.app.data.repository.ReadingProgressRepository;
import com.monogatari.app.data.repository.StoryRepository;
import com.monogatari.app.databinding.ActivityStoryDetailBinding;
import com.monogatari.app.ui.adapter.ChapterAdapter;
import com.monogatari.app.ui.viewmodel.StoryViewModel;
import com.monogatari.app.ui.viewmodel.StoryViewModelFactory;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryDetailActivity extends AppCompatActivity {
    private ActivityStoryDetailBinding binding;
    private StoryViewModel storyViewModel;
    private ChapterAdapter chapterAdapter;
    private FollowApi followApi; // CHANGED FROM SocialApi
    private StoryResponse story;
    private boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        story = (StoryResponse) getIntent().getSerializableExtra("story_data");

        if (story != null) {
            initViewModel();
            displayStoryInfo();
            setupRecyclerView();
            setupObservers();

            storyViewModel.loadChapters(story.getId());
            checkFollow();

            binding.btnFollow.setOnClickListener(v -> toggleFollow());
        } else {
            Toast.makeText(this, "Story data is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayStoryInfo() {
        binding.tvDetailTitle.setText(story.getTitle());
        binding.tvDetailAuthor.setText("By " + story.getAuthorName());
        binding.tvDetailDescription.setText(story.getDescription());

        Glide.with(this)
                .load(story.getCoverUrl())
                .into(binding.ivDetailCover);
    }

    private void setupRecyclerView() {
        chapterAdapter = new ChapterAdapter(new ArrayList<>(), chapter -> {
            Intent intent = new Intent(StoryDetailActivity.this, ReadingActivity.class);
            intent.putExtra("story_id", story.getId());
            intent.putExtra("chapter_id", chapter.getId());
            intent.putExtra("story_title", story.getTitle());
            startActivity(intent);
        });
        binding.rvChapters.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChapters.setAdapter(chapterAdapter);
    }

    private void setupObservers() {
        storyViewModel.getChapters().observe(this, chapterList -> {
            if (chapterList != null) {
                chapterAdapter.setChapters(chapterList);
            }
        });
    }

    private void initViewModel() {
        // 1. Initialize all APIs
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        ChapterApi chapterApi = ApiClient.getClient(this).create(ChapterApi.class);
        CommentApi commentApi = ApiClient.getClient(this).create(CommentApi.class);
        RatingApi ratingApi = ApiClient.getClient(this).create(RatingApi.class);
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        GenreApi genreApi = ApiClient.getClient(this).create(GenreApi.class);
        AuthorApi authorApi = ApiClient.getClient(this).create(AuthorApi.class);
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        PaymentApi paymentApi = ApiClient.getClient(this).create(PaymentApi.class);

        // 2. Initialize all 7 Repositories
        StoryRepository storyRepo = new StoryRepository(storyApi);
        ChapterRepository chapterRepo = new ChapterRepository(chapterApi);
        CommentRepository commentRepo = new CommentRepository(commentApi);
        RatingRepository ratingRepo = new RatingRepository(ratingApi);
        ReadingProgressRepository progressRepo = new ReadingProgressRepository(progressApi);
        GenreRepository genreRepo = new GenreRepository(genreApi);
        AuthorRepository authorRepo = new AuthorRepository(authorApi);
        AiRepository aiRepo = new AiRepository(aiApi);
        PaymentRepository paymentRepo = new PaymentRepository(paymentApi);

        // 3. Initialize FollowApi separately as it is used directly in Activity
        followApi = ApiClient.getClient(this).create(FollowApi.class);

        // 4. Pass all 7 repositories to the factory
        StoryViewModelFactory factory = new StoryViewModelFactory(
                storyRepo,
                chapterRepo,
                commentRepo,
                ratingRepo,
                progressRepo,
                genreRepo,
                authorRepo,
                aiRepo,
                paymentRepo
        );

        // 5. Get ViewModel
        storyViewModel = new ViewModelProvider(this, factory).get(StoryViewModel.class);
    }

    private void checkFollow() {
        followApi.checkFollowStatus(story.getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFollowing = response.body();
                    updateFollowIcon();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(StoryDetailActivity.this, "Status Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFollow() {
        followApi.toggleFollow(story.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    isFollowing = !isFollowing;
                    updateFollowIcon();
                    Toast.makeText(StoryDetailActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(StoryDetailActivity.this, "Toggle Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFollowIcon() {
        binding.btnFollow.setImageResource(isFollowing ?
                android.R.drawable.btn_star_big_on :
                android.R.drawable.btn_star_big_off);
    }
}