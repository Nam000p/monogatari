package com.monogatari.app.ui.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.monogatari.app.R;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.FollowApi;
import com.monogatari.app.data.api.RatingApi;
import com.monogatari.app.data.api.ReadingProgressApi;
import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.data.model.progress.ReadingProgressResponse;
import com.monogatari.app.data.model.rating.RatingResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.FollowRepository;
import com.monogatari.app.data.repository.RatingRepository;
import com.monogatari.app.data.repository.ReadingProgressRepository;
import com.monogatari.app.data.repository.UserRepository;
import com.monogatari.app.databinding.ActivityStoryDetailBinding;
import com.monogatari.app.ui.adapter.ChapterAdapter;
import com.monogatari.app.ui.viewmodel.RatingViewModel;
import com.monogatari.app.ui.viewmodel.RatingViewModelFactory;
import com.monogatari.app.ui.viewmodel.StoryDetailViewModel;
import com.monogatari.app.ui.viewmodel.StoryDetailViewModelFactory;
import com.monogatari.app.ui.viewmodel.UserViewModel;
import com.monogatari.app.ui.viewmodel.UserViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryDetailActivity extends AppCompatActivity implements ChapterAdapter.OnChapterClickListener {
    public static final String EXTRA_STORY_ID = "extra_story_id";

    private ActivityStoryDetailBinding binding;
    private StoryDetailViewModel storyViewModel;
    private UserViewModel userViewModel;
    private RatingViewModel ratingViewModel;
    private ChapterAdapter chapterAdapter;
    private Long storyId;
    private Long currentAuthorId;
    private Long resumeChapterId = null;
    private int resumePage = 1;
    private List<ChapterResponse> currentChapters = new ArrayList<>();

    private boolean isUserPremium = false;
    private boolean isProgressLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storyId = getIntent().getLongExtra(EXTRA_STORY_ID, -1);
        if (storyId == -1) {
            Toast.makeText(this, "Story not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupRecyclerView();
        setupViewModels();
        setupClickListeners();

        storyViewModel.fetchAllStoryData(storyId);
        userViewModel.fetchProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (storyId != -1 && !isProgressLoading) {
            fetchReadingProgress();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        chapterAdapter = new ChapterAdapter(this);
        binding.rvChapters.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChapters.setAdapter(chapterAdapter);
    }

    private void setupViewModels() {
        StoryDetailViewModelFactory storyFactory = new StoryDetailViewModelFactory(this);
        storyViewModel = new ViewModelProvider(this, storyFactory).get(StoryDetailViewModel.class);

        UserApi userApi = ApiClient.getClient(this).create(UserApi.class);
        FollowApi followApi = ApiClient.getClient(this).create(FollowApi.class);
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        UserRepository userRepository = new UserRepository(userApi);
        FollowRepository followRepository = new FollowRepository(followApi);
        ReadingProgressRepository progressRepository = new ReadingProgressRepository(progressApi);
        UserViewModelFactory userFactory = new UserViewModelFactory(userRepository, followRepository, progressRepository);
        userViewModel = new ViewModelProvider(this, userFactory).get(UserViewModel.class);

        RatingApi ratingApi = ApiClient.getClient(this).create(RatingApi.class);
        RatingRepository ratingRepo = new RatingRepository(ratingApi);
        RatingViewModelFactory ratingFactory = new RatingViewModelFactory(ratingRepo);
        ratingViewModel = new ViewModelProvider(this, ratingFactory).get(RatingViewModel.class);

        observeViewModels();

        ratingViewModel.fetchMyRating(storyId);
    }

    private void fetchReadingProgress() {
        isProgressLoading = true;
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        ReadingProgressRepository progressRepo = new ReadingProgressRepository(progressApi);

        progressRepo.getProgress(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ReadingProgressResponse> call, @NonNull Response<ReadingProgressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resumeChapterId = response.body().getChapterId();
                    resumePage = response.body().getLastPage();
                    updateContinueButton();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReadingProgressResponse> call, @NonNull Throwable t) {}
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateContinueButton() {
        if (resumeChapterId == null) {
            binding.btnRead.setText("Read First Chapter");
            return;
        }

        String chapterInfo = "";
        for (ChapterResponse chapter : currentChapters) {
            if (chapter.getId().equals(resumeChapterId)) {
                chapterInfo = "Ch " + chapter.getChapterNumber();
                break;
            }
        }

        if (!chapterInfo.isEmpty()) {
            binding.btnRead.setText("Continue: " + chapterInfo + " - Page " + resumePage);
        } else {
            binding.btnRead.setText("Continue Reading");
        }
    }

    @SuppressLint("SetTextI18n")
    private void observeViewModels() {
        storyViewModel.getStoryDetails().observe(this, this::bindStoryDetails);

        storyViewModel.getChapters().observe(this, chapters -> {
            if (chapters != null) {
                this.currentChapters = chapters;
                chapterAdapter.setChapters(chapters);
                updateContinueButton();
            }
        });

        storyViewModel.getIsFollowed().observe(this, isFollowed -> {
            if (isFollowed != null && isFollowed) {
                binding.btnFollow.setIconResource(android.R.drawable.ic_menu_delete);
                binding.btnFollow.setText("Following");
                binding.btnFollow.setTextColor(getResources().getColor(R.color.netflix_red, null));
                binding.btnFollow.setIconTintResource(R.color.netflix_red);
            } else {
                binding.btnFollow.setIconResource(android.R.drawable.ic_input_add);
                binding.btnFollow.setText("Add to My List");
                binding.btnFollow.setTextColor(getResources().getColor(R.color.text_main, null));
                binding.btnFollow.setIconTintResource(R.color.text_main);
            }
        });

        userViewModel.getProfile().observe(this, profile -> {
            if (profile != null) this.isUserPremium = profile.isPremium();
        });
    }

    @SuppressLint("SetTextI18n")
    private void bindStoryDetails(StoryResponse story) {
        if (story == null) return;
        Glide.with(this).load(story.getCoverUrl()).into(binding.ivCover);
        binding.tvTitle.setText(story.getTitle());
        binding.tvRating.setText(story.getAverageRating() != null ? (int)(story.getAverageRating() * 20) + "% Match" : "New");
        binding.tvAgeLimit.setText(story.getAgeLimit() != null ? story.getAgeLimit() + "+" : "All");
        binding.tvType.setText(story.getType() != null ? story.getType().name() : "");
        binding.tvStatus.setText(story.getStatus() != null ? story.getStatus().name() : "");
        binding.tvDescription.setText(story.getDescription());

        if (story.getAuthorId() != null) {
            this.currentAuthorId = story.getAuthorId();
            binding.tvAuthorName.setText("Author: " + story.getAuthorName());
            binding.tvAuthorName.setPaintFlags(binding.tvAuthorName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    private void setupClickListeners() {
        binding.btnRead.setOnClickListener(v -> {
            if (resumeChapterId != null) {
                navigateToReader(resumeChapterId, resumePage);
            } else if (!currentChapters.isEmpty()) {
                navigateToReader(currentChapters.get(0).getId(), 1);
            } else {
                Toast.makeText(this, "No chapters available.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvAuthorName.setOnClickListener(v -> {
            if (currentAuthorId != null) {
                Intent intent = new Intent(this, AuthorActivity.class);
                intent.putExtra("EXTRA_AUTHOR_ID", currentAuthorId);
                startActivity(intent);
            }
        });

        binding.btnFollow.setOnClickListener(v -> storyViewModel.toggleFollow(storyId));

        binding.tvRating.setOnClickListener(v -> showRatingDialog());
    }

    @Override
    public void onChapterClick(ChapterResponse chapter) {
        navigateToReader(chapter.getId(), resumePage);
    }

    private void navigateToReader(Long targetChapterId, int page) {
        boolean isPremium = false;
        for (ChapterResponse c : currentChapters) {
            if (c.getId().equals(targetChapterId)) {
                isPremium = c.getIsPremium() != null && c.getIsPremium();
                break;
            }
        }

        if (isPremium && !isUserPremium) {
            Toast.makeText(this, "Upgrade to Premium to read this!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(ReaderActivity.EXTRA_STORY_ID, storyId);
        intent.putExtra(ReaderActivity.EXTRA_CHAPTER_ID, targetChapterId);
        intent.putExtra(ReaderActivity.EXTRA_STORY_TITLE, binding.tvTitle.getText().toString());
        intent.putExtra(ReaderActivity.EXTRA_START_PAGE, page);
        startActivity(intent);
    }

    private void showRatingDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rating, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.dialogRatingBar);
        EditText etReview = dialogView.findViewById(R.id.etReview);

        RatingResponse current = ratingViewModel.getMyRating().getValue();
        if (current != null) {
            ratingBar.setRating(current.getScore());
            etReview.setText(current.getReview());
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Rate this Story")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    int score = (int) ratingBar.getRating();
                    String review = etReview.getText().toString();

                    if (score < 1) {
                        Toast.makeText(this, "Please select at least 1 star", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ratingViewModel.submitRating(storyId, score, review);
                    Toast.makeText(this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}