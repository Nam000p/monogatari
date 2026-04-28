package com.monogatari.app.ui.view;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.databinding.ActivityReaderBinding;
import com.monogatari.app.ui.adapter.ReaderAdapter;
import com.monogatari.app.ui.adapter.ReaderItem;
import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    private ActivityReaderBinding binding;
    private ReaderAdapter adapter;
    private LinearLayoutManager layoutManager;
    private boolean isOverlayVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();
        setupRecyclerView();
        setupControls();

        loadMockData();
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        binding.rvReaderPages.setLayoutManager(layoutManager);

        adapter = new ReaderAdapter(this::toggleOverlayVisibility);
        binding.rvReaderPages.setAdapter(adapter);

        binding.rvReaderPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentPosition = layoutManager.findFirstVisibleItemPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    binding.tvCurrentPage.setText(String.valueOf(currentPosition + 1));
                    binding.seekBarProgress.setProgress(currentPosition);
                }
            }
        });
    }

    private void setupControls() {
        binding.seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.rvReaderPages.scrollToPosition(progress);
                    binding.tvCurrentPage.setText(String.valueOf(progress + 1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        binding.btnNextChapter.setOnClickListener(v -> {
            Toast.makeText(this, "Loading Next Chapter", Toast.LENGTH_SHORT).show();
        });

        binding.btnPreviousChapter.setOnClickListener(v -> {
            Toast.makeText(this, "Loading Previous Chapter", Toast.LENGTH_SHORT).show();
        });

        binding.btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings Opened", Toast.LENGTH_SHORT).show();
        });
    }

    private void processChapterData(ChapterResponse chapter) {
        List<ReaderItem> items = new ArrayList<>();

        binding.tvReaderChapterTitle.setText(chapter.getTitle());

        if (chapter.getContent() != null && !chapter.getContent().isEmpty()) {
            items.add(new ReaderItem(ReaderItem.TYPE_TEXT, chapter.getContent()));
        }

        if (chapter.getImageUrls() != null && !chapter.getImageUrls().isEmpty()) {
            for (String url : chapter.getImageUrls()) {
                items.add(new ReaderItem(ReaderItem.TYPE_IMAGE, url));
            }
        }

        adapter.submitList(items);

        int totalItems = items.size();
        binding.tvTotalPages.setText(String.valueOf(totalItems));
        binding.seekBarProgress.setMax(totalItems > 0 ? totalItems - 1 : 0);
    }

    private void loadMockData() {
        ChapterResponse mockChapter = new ChapterResponse();
        mockChapter.setTitle("Chapter 1: The Beginning");
        mockChapter.setContent("Sample text content.");

        List<String> mockImages = new ArrayList<>();
        mockImages.add("https://example.com/page1.jpg");
        mockImages.add("https://example.com/page2.jpg");
        mockChapter.setImageUrls(mockImages);

        processChapterData(mockChapter);
    }

    private void toggleOverlayVisibility() {
        isOverlayVisible = !isOverlayVisible;
        int visibility = isOverlayVisible ? View.VISIBLE : View.GONE;
        binding.appBarLayoutOverlay.setVisibility(visibility);
        binding.bottomControlsOverlay.setVisibility(visibility);

        if (!isOverlayVisible) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}