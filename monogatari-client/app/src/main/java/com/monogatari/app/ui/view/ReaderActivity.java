package com.monogatari.app.ui.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.ChapterApi;
import com.monogatari.app.data.api.ReadingProgressApi;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.data.model.progress.ReadingProgressRequest;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.repository.ChapterRepository;
import com.monogatari.app.data.repository.ReadingProgressRepository;
import com.monogatari.app.data.repository.StoryRepository;
import com.monogatari.app.databinding.ActivityReaderBinding;
import com.monogatari.app.ui.adapter.ReaderAdapter;
import com.monogatari.app.ui.adapter.ReaderItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {
    public static final String EXTRA_STORY_ID = "extra_story_id";
    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
    public static final String EXTRA_STORY_TITLE = "extra_story_title";
    public static final String EXTRA_START_PAGE = "extra_start_page";

    private final Handler hideHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideRunnable = this::hideControls;

    private final Handler saveHandler = new Handler(Looper.getMainLooper());
    private Runnable saveRunnable;

    private ActivityReaderBinding binding;
    private ReaderAdapter adapter;
    private ChapterRepository chapterRepo;
    private ReadingProgressRepository progressRepo;
    private StoryRepository storyRepo;

    private long storyId, chapterId;
    private int startPage = 1;
    private int lastSavedPage = -1;
    private boolean isUserPremium = false;
    private boolean isControlsVisible = false;

    private boolean isJumpingToPage = true;

    private List<ChapterResponse> chapterList = new ArrayList<>();
    private int currentChapterIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storyId = getIntent().getLongExtra(EXTRA_STORY_ID, -1);
        chapterId = getIntent().getLongExtra(EXTRA_CHAPTER_ID, -1);
        startPage = getIntent().getIntExtra(EXTRA_START_PAGE, 1);

        lastSavedPage = startPage;

        setupRepos();
        setupRecyclerView();
        setupListeners();
        fetchUserProfile();
        loadChapterList();
        loadChapter(chapterId);
    }

    private void setupRepos() {
        ChapterApi api = ApiClient.getClient(this).create(ChapterApi.class);
        ReadingProgressApi pApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        chapterRepo = new ChapterRepository(api);
        progressRepo = new ReadingProgressRepository(pApi);
        storyRepo = new StoryRepository(storyApi);
    }

    private void setupRecyclerView() {
        adapter = new ReaderAdapter();
        binding.rvReaderPages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReaderPages.setAdapter(adapter);

        binding.rvReaderPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isJumpingToPage) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (lm != null) {
                        int currentPos = lm.findFirstVisibleItemPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            updateProgressUI(currentPos + 1);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm != null) {
                    int currentPos = lm.findFirstVisibleItemPosition();
                    if (currentPos != RecyclerView.NO_POSITION) {
                        binding.tvCurrentPage.setText(String.valueOf(currentPos + 1));
                        binding.seekBarProgress.setProgress(currentPos);
                    }
                }

                if (Math.abs(dy) > 20) hideControls();
            }
        });
    }

    private void loadChapter(long id) {
        this.chapterId = id;
        isJumpingToPage = true;

        chapterRepo.getChapter(storyId, chapterId).enqueue(new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ChapterResponse> call, @NonNull Response<ChapterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChapterResponse data = response.body();
                    binding.tvReaderChapterTitle.setText("Chapter " + data.getChapterNumber());

                    List<ReaderItem> items = new ArrayList<>();
                    if (data.getImageUrls() != null) {
                        for (String url : data.getImageUrls()) items.add(new ReaderItem(ReaderItem.TYPE_IMAGE, url));
                    }
                    if (data.getContent() != null) items.add(new ReaderItem(ReaderItem.TYPE_TEXT, data.getContent()));

                    adapter.setItems(items);

                    binding.rvReaderPages.post(() -> {
                        LinearLayoutManager lm = (LinearLayoutManager) binding.rvReaderPages.getLayoutManager();
                        if (lm != null) {
                            int target = (startPage > 0 && startPage <= items.size()) ? startPage - 1 : 0;
                            lm.scrollToPositionWithOffset(target, 0);
                        }

                        new Handler(Looper.getMainLooper()).postDelayed(() -> isJumpingToPage = false, 1000);
                    });

                    binding.tvTotalPages.setText(String.valueOf(items.size()));
                    binding.seekBarProgress.setMax(!items.isEmpty() ? items.size() - 1 : 0);
                    updateButtonStates();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChapterResponse> call, @NonNull Throwable t) {
                isJumpingToPage = false;
            }
        });
    }

    private void updateProgressUI(int page) {
        if (page == lastSavedPage || isJumpingToPage) return;

        binding.tvCurrentPage.setText(String.valueOf(page));
        binding.seekBarProgress.setProgress(page - 1);
        lastSavedPage = page;

        saveHandler.removeCallbacks(saveRunnable);
        saveRunnable = () -> saveProgress(page);
        saveHandler.postDelayed(saveRunnable, 800);
    }

    private void saveProgress(int page) {
        android.util.Log.d("READER_DEBUG", "Page: " + page);

        ReadingProgressRequest req = new ReadingProgressRequest();
        req.setStoryId(storyId);
        req.setChapterId(chapterId);
        req.setLastPage(page);

        progressRepo.updateProgress(storyId, req).enqueue(new Callback<>() {
            @Override public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {}
            @Override public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {}
        });
    }

    private void fetchUserProfile() {
        UserApi userApi = ApiClient.getClient(this).create(UserApi.class);
        userApi.getMyProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) isUserPremium = response.body().isPremium();
            }
            @Override public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadChapterList() {
        storyRepo.getChapters(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ChapterResponse>> call, @NonNull Response<List<ChapterResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chapterList = response.body();
                    chapterList.sort((c1, c2) -> Float.compare(c1.getChapterNumber(), c2.getChapterNumber()));
                    updateButtonStates();
                }
            }
            @Override public void onFailure(@NonNull Call<List<ChapterResponse>> call, @NonNull Throwable t) {}
        });
    }

    private void updateButtonStates() {
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getId() == chapterId) { currentChapterIndex = i; break; }
        }
        binding.btnPreviousChapter.setEnabled(currentChapterIndex > 0);
        binding.btnNextChapter.setEnabled(currentChapterIndex < chapterList.size() - 1);
    }

    private void navigateToChapter(ChapterResponse target) {
        if (target.getIsPremium() && !isUserPremium) {
            Toast.makeText(this, "Upgrade to read this chapter!", Toast.LENGTH_SHORT).show();
            return;
        }
        startPage = 1;
        lastSavedPage = -1;
        loadChapter(target.getId());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        binding.toolbarReader.setNavigationOnClickListener(v -> onBackPressed());
        binding.rvReaderPages.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                if (isControlsVisible) hideControls(); else showControls();
            }
            return false;
        });

        binding.btnPreviousChapter.setOnClickListener(v -> {
            if (currentChapterIndex > 0) navigateToChapter(chapterList.get(currentChapterIndex - 1));
        });

        binding.btnNextChapter.setOnClickListener(v -> {
            if (currentChapterIndex < chapterList.size() - 1) navigateToChapter(chapterList.get(currentChapterIndex + 1));
        });

        binding.btnComments.setOnClickListener(v -> {
            CommentBottomSheetFragment.newInstance(chapterId)
                    .show(getSupportFragmentManager(), "CommentBottomSheet");
        });

        binding.seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int p, boolean fromU) {
                if (fromU) {
                    isJumpingToPage = true;
                    binding.rvReaderPages.scrollToPosition(p);
                    updateProgressUI(p + 1);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> isJumpingToPage = false, 500);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });
    }

    private void showControls() {
        isControlsVisible = true;
        binding.appBarLayoutOverlay.setVisibility(View.VISIBLE);
        binding.bottomControlsOverlay.setVisibility(View.VISIBLE);
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, 3500);
    }

    private void hideControls() {
        isControlsVisible = false;
        binding.appBarLayoutOverlay.setVisibility(View.GONE);
        binding.bottomControlsOverlay.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveHandler.removeCallbacks(saveRunnable);
        if (!isJumpingToPage) {
            LinearLayoutManager lm = (LinearLayoutManager) binding.rvReaderPages.getLayoutManager();
            if (lm != null) {
                int currentPos = lm.findFirstVisibleItemPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    saveProgress(currentPos + 1);
                }
            }
        }
    }
}