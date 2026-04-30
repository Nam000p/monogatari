package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.monogatari.app.R;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.GenreApi;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.model.genre.GenreResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.GenreRepository;
import com.monogatari.app.data.repository.StoryRepository;
import com.monogatari.app.databinding.FragmentHomeBinding;
import com.monogatari.app.ui.adapter.BannerAdapter;
import com.monogatari.app.ui.adapter.StoryAdapter;
import com.monogatari.app.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements StoryAdapter.OnStoryClickListener, BannerAdapter.OnBannerClickListener {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private BannerAdapter bannerAdapter;
    private StoryAdapter trendingAdapter;
    private StoryAdapter newestAdapter;
    private StoryAdapter topRatedAdapter;

    private List<GenreResponse> genreList = new ArrayList<>();
    private boolean isSpinnerInitial = true;
    private List<StoryResponse> currentBannerStories = new ArrayList<>();
    private StoryResponse currentFeaturedStory;

    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding != null && bannerAdapter != null) {
                int itemCount = bannerAdapter.getItemCount();
                if (itemCount > 0) {
                    int nextItem = (binding.vpBanner.getCurrentItem() + 1) % itemCount;
                    binding.vpBanner.setCurrentItem(nextItem, true);
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupAdapters();
        setupClickListeners();
        setupViewModel();

        fetchGenresAndSetupSpinner();

        if (viewModel != null) {
            viewModel.fetchAllHomeData();
        }
    }

    private void fetchGenresAndSetupSpinner() {
        GenreRepository genreRepo = new GenreRepository(ApiClient.getClient(requireContext()).create(GenreApi.class));

        genreRepo.getAllGenres().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<GenreResponse>> call, @NonNull Response<List<GenreResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genreList = response.body();
                    setupSpinnerUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GenreResponse>> call, @NonNull Throwable t) {

            }
        });
    }

    private void setupSpinnerUI() {
        List<String> genreNames = new ArrayList<>();
        genreNames.add("Categories");

        for (GenreResponse genre : genreList) {
            genreNames.add(genre.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_selected, genreNames);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        binding.spinnerGenre.setAdapter(adapter);

        binding.spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerInitial) {
                    isSpinnerInitial = false;
                    return;
                }

                if (position > 0) {
                    GenreResponse selectedGenre = genreList.get(position - 1);

                    Intent intent = new Intent(requireContext(), GenreFilterActivity.class);
                    intent.putExtra("EXTRA_GENRE_ID", selectedGenre.getId());
                    intent.putExtra("EXTRA_GENRE_NAME", selectedGenre.getName());
                    startActivity(intent);

                    binding.spinnerGenre.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupViewModel() {
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(HomeViewModel.class)) {
                    StoryApi api = ApiClient.getClient(requireContext()).create(StoryApi.class);
                    StoryRepository repository = new StoryRepository(api);
                    return (T) new HomeViewModel(repository);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        };

        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
        observeViewModel();
    }

    private void setupAdapters() {
        bannerAdapter = new BannerAdapter(this);
        binding.vpBanner.setAdapter(bannerAdapter);

        binding.vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (!currentBannerStories.isEmpty()) {
                    updateFeaturedMetadata(currentBannerStories.get(position));
                }

                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000);
            }
        });

        trendingAdapter = new StoryAdapter(this);
        binding.rvTrending.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrending.setAdapter(trendingAdapter);

        newestAdapter = new StoryAdapter(this);
        binding.rvNewest.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNewest.setAdapter(newestAdapter);

        topRatedAdapter = new StoryAdapter(this);
        binding.rvTopRated.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTopRated.setAdapter(topRatedAdapter);
    }

    private void observeViewModel() {
        viewModel.getTrendingStories().observe(getViewLifecycleOwner(), stories -> {
            if (stories != null && !stories.isEmpty()) {
                trendingAdapter.setStories(stories);

                int bannerCount = Math.min(stories.size(), 5);
                currentBannerStories = stories.subList(0, bannerCount);
                bannerAdapter.setBannerStories(currentBannerStories);

                updateFeaturedMetadata(currentBannerStories.get(0));
            }
        });

        viewModel.getNewestStories().observe(getViewLifecycleOwner(), stories -> {
            if (stories != null && !stories.isEmpty()) {
                newestAdapter.setStories(stories);
            }
        });

        viewModel.getTopRatedStories().observe(getViewLifecycleOwner(), stories -> {
            if (stories != null && !stories.isEmpty()) {
                topRatedAdapter.setStories(stories);
            }
        });
    }

    private void updateFeaturedMetadata(StoryResponse featuredStory) {
        this.currentFeaturedStory = featuredStory;

        if (featuredStory.getGenres() != null && !featuredStory.getGenres().isEmpty()) {
            binding.tvFeaturedGenres.setText(TextUtils.join(" • ", featuredStory.getGenres()));
        } else {
            binding.tvFeaturedGenres.setText("");
        }
    }

    private void setupClickListeners() {
        binding.btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ExploreActivity.class);
            startActivity(intent);
        });

        binding.btnReadNow.setOnClickListener(v -> {
            if (currentFeaturedStory != null) {
                Intent intent = new Intent(requireContext(), StoryDetailActivity.class);
                intent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, currentFeaturedStory.getId());
                startActivity(intent);
            }
        });

        binding.btnAiSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AiChatActivity.class);
            intent.putExtra("CHAT_MODE", "STORY_SEARCH");
            startActivity(intent);
        });

        binding.btnMyList.setOnClickListener(v -> {
            if (currentFeaturedStory != null) {
                Toast.makeText(requireContext(), "Added to My List: " + currentFeaturedStory.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStoryClick(StoryResponse story) {
        if (story != null && story.getId() != null) {
            Intent intent = new Intent(requireContext(), StoryDetailActivity.class);
            intent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onBannerClick(StoryResponse story) {
        if (story != null && story.getId() != null) {
            Intent intent = new Intent(requireContext(), StoryDetailActivity.class);
            intent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
        binding = null;
    }
}