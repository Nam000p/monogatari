package com.monogatari.app.ui.view;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.monogatari.app.data.api.*;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.data.repository.*;
import com.monogatari.app.databinding.ActivityReadingBinding;
import com.monogatari.app.ui.adapter.MangaAdapter;
import com.monogatari.app.ui.viewmodel.StoryViewModel;
import com.monogatari.app.ui.viewmodel.StoryViewModelFactory;
import java.util.List;

public class ReadingActivity extends AppCompatActivity {
    private ActivityReadingBinding binding;
    private StoryViewModel storyViewModel;
    private long storyId;
    private long currentChapterId;
    private List<ChapterResponse> chapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storyId = getIntent().getLongExtra("story_id", -1);
        currentChapterId = getIntent().getLongExtra("chapter_id", -1);
        String storyTitle = getIntent().getStringExtra("story_title");

        setSupportActionBar(binding.toolbarReading);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(storyTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViewModel();
        setupObservers();
        setupNavigationButtons();

        if (storyId != -1 && currentChapterId != -1) {
            storyViewModel.loadChapterDetail(storyId, currentChapterId);
            storyViewModel.loadChapters(storyId);
        }
    }

    private void setupNavigationButtons() {
        binding.btnPrevious.setOnClickListener(v -> navigateChapter(-1));
        binding.btnNext.setOnClickListener(v -> navigateChapter(1));
    }

    private void navigateChapter(int direction) {
        if (chapterList == null || chapterList.isEmpty()) return;
        int currentIndex = -1;
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getId() == currentChapterId) {
                currentIndex = i;
                break;
            }
        }

        int targetIndex = currentIndex + direction;
        if (targetIndex >= 0 && targetIndex < chapterList.size()) {
            currentChapterId = chapterList.get(targetIndex).getId();
            storyViewModel.loadChapterDetail(storyId, currentChapterId);
        } else {
            Toast.makeText(this, "No more chapters", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupObservers() {
        storyViewModel.getChapterDetail().observe(this, chapter -> {
            if (chapter != null) displayContent(chapter);
        });

        storyViewModel.getChapters().observe(this, chapters -> {
            this.chapterList = chapters;
            updateNavigationUI();
        });
    }

    private void updateNavigationUI() {
        if (chapterList == null) return;
        int index = -1;
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getId() == currentChapterId) { index = i; break; }
        }
        binding.btnPrevious.setEnabled(index > 0);
        binding.btnNext.setEnabled(index < chapterList.size() - 1);
    }

    private void displayContent(ChapterResponse chapter) {
        // 1. Reset về đầu trang khi load chapter mới
        binding.mainContentScrollView.scrollTo(0, 0);

        // 2. Xử lý hiển thị Text (Cho Light Novel)
        if (chapter.getContent() != null && !chapter.getContent().trim().isEmpty()) {
            binding.tvTextContent.setVisibility(View.VISIBLE);
            binding.tvTextContent.setText(chapter.getContent());
        } else {
            binding.tvTextContent.setVisibility(View.GONE);
        }

        // 3. Xử lý hiển thị Ảnh (Cho Manga hoặc minh họa)
        if (chapter.getImageUrls() != null && !chapter.getImageUrls().isEmpty()) {
            binding.rvImageContent.setVisibility(View.VISIBLE);

            MangaAdapter mangaAdapter = new MangaAdapter(chapter.getImageUrls());
            binding.rvImageContent.setLayoutManager(new LinearLayoutManager(this));
            binding.rvImageContent.setAdapter(mangaAdapter);

            // QUAN TRỌNG: Phải disable cái này vì đã có ScrollView bên ngoài gánh rồi
            binding.rvImageContent.setNestedScrollingEnabled(false);
        } else {
            binding.rvImageContent.setVisibility(View.GONE);
        }
    }

    private void initViewModel() {
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        ChapterApi chapterApi = ApiClient.getClient(this).create(ChapterApi.class);
        CommentApi commentApi = ApiClient.getClient(this).create(CommentApi.class);
        RatingApi ratingApi = ApiClient.getClient(this).create(RatingApi.class);
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        GenreApi genreApi = ApiClient.getClient(this).create(GenreApi.class);
        AuthorApi authorApi = ApiClient.getClient(this).create(AuthorApi.class);
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        PaymentApi paymentApi = ApiClient.getClient(this).create(PaymentApi.class);

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
}