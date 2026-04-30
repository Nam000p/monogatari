package com.monogatari.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.CommentApi;
import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.model.enums.SystemRole;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.repository.CommentRepository;
import com.monogatari.app.databinding.LayoutCommentBottomSheetBinding;
import com.monogatari.app.ui.adapter.CommentAdapter;
import com.monogatari.app.ui.viewmodel.CommentViewModel;
import com.monogatari.app.ui.viewmodel.CommentViewModelFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentBottomSheetFragment extends BottomSheetDialogFragment {
    private LayoutCommentBottomSheetBinding binding;
    private CommentAdapter adapter;
    private CommentViewModel viewModel;
    private long chapterId;

    public static CommentBottomSheetFragment newInstance(long chapterId) {
        CommentBottomSheetFragment fragment = new CommentBottomSheetFragment();
        Bundle args = new Bundle();
        args.putLong("chapter_id", chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutCommentBottomSheetBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            chapterId = getArguments().getLong("chapter_id");
        }

        // 1. Initialize Repos & ViewModel
        CommentApi api = ApiClient.getClient(requireContext()).create(CommentApi.class);
        CommentRepository repository = new CommentRepository(api);
        viewModel = new ViewModelProvider(this, new CommentViewModelFactory(repository)).get(CommentViewModel.class);

        // 2. Setup UI and Observers
        setupRecyclerView();
        observeViewModel();

        // 3. Fetch Data
        fetchCurrentUser();
        viewModel.fetchComments(chapterId);

        binding.btnSendComment.setOnClickListener(v -> postComment());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new CommentAdapter();
        adapter.setOnDeleteListener(commentId -> new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Comment")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (d, w) -> viewModel.deleteComment(chapterId, commentId))
                .setNegativeButton("Cancel", null)
                .show());

        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        viewModel.getComments().observe(getViewLifecycleOwner(), list -> {
            if (list != null) adapter.setComments(list);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) android.widget.Toast.makeText(getContext(), error, android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchCurrentUser() {
        UserApi userApi = ApiClient.getClient(requireContext()).create(UserApi.class);
        userApi.getMyProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setUserInfo(response.body().getId(), response.body().getRole() == SystemRole.ROLE_ADMIN);
                }
            }
            @Override public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {}
        });
    }

    private void postComment() {
        String content = binding.etCommentInput.getText().toString().trim();
        if (content.isEmpty()) return;

        viewModel.postComment(chapterId, content);
        binding.etCommentInput.setText("");
    }
}