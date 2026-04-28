package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.monogatari.app.databinding.ActivityStoryDetailBinding;

public class StoryDetailActivity extends AppCompatActivity {

    private ActivityStoryDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        binding.rvChapters.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChapters.setNestedScrollingEnabled(false);
        // TODO: Initialize ChapterAdapter and set it to rvChapters
    }

    private void setupListeners() {
        binding.btnRead.setOnClickListener(v -> {
            startActivity(new Intent(this, ReaderActivity.class));
        });

        binding.btnFollow.setOnClickListener(v -> {
            Toast.makeText(this, "Added to My List", Toast.LENGTH_SHORT).show();
            // TODO: Toggle follow state in ViewModel
        });
    }
}