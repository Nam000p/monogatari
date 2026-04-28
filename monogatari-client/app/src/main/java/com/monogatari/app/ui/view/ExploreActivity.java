package com.monogatari.app.ui.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monogatari.app.databinding.ActivityExploreBinding;

public class ExploreActivity extends AppCompatActivity {

    private ActivityExploreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupListeners();
    }

    private void setupRecyclerView() {
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        binding.btnClear.setOnClickListener(v -> {
            binding.etSearchQuery.setText("");
        });
    }
}