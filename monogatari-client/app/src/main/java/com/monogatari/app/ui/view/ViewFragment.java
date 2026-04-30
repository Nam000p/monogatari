package com.monogatari.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.StoryRepository;
import com.monogatari.app.databinding.FragmentViewBinding;
import com.monogatari.app.ui.adapter.StoryListAdapter;
import com.monogatari.app.ui.viewmodel.HomeViewModel;

public class ViewFragment extends Fragment implements StoryListAdapter.OnStoryClickListener {

    private FragmentViewBinding binding;
    private HomeViewModel viewModel;

    private StoryListAdapter newestAdapter;
    private StoryListAdapter topRatedAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViews();
        setupViewModel();

        if (viewModel != null) {
            viewModel.fetchAllHomeData();
        }
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

    private void setupRecyclerViews() {
        newestAdapter = new StoryListAdapter(this);
        binding.rvNewest.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvNewest.setAdapter(newestAdapter);

        topRatedAdapter = new StoryListAdapter(this);
        binding.rvTopRated.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvTopRated.setAdapter(topRatedAdapter);
    }

    private void observeViewModel() {
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

    @Override
    public void onStoryClick(StoryResponse story) {
        if (story != null && story.getId() != null) {
            android.content.Intent intent = new android.content.Intent(requireContext(), StoryDetailActivity.class);
            intent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}