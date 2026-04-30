package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.model.comment.CommentRequest;
import com.monogatari.app.data.model.comment.CommentResponse;
import com.monogatari.app.data.repository.CommentRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentViewModel extends ViewModel {
    private final CommentRepository repository;
    private final MutableLiveData<List<CommentResponse>> comments = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public CommentViewModel(CommentRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<CommentResponse>> getComments() { return comments; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }

    public void fetchComments(Long chapterId) {
        isLoading.setValue(true);
        repository.getComments(chapterId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CommentResponse>> call, @NonNull Response<List<CommentResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    comments.setValue(response.body());
                } else {
                    error.setValue("Failed to load comments");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CommentResponse>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void postComment(Long chapterId, String content) {
        CommentRequest request = new CommentRequest();
        request.setContent(content);
        repository.addComment(chapterId, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    fetchComments(chapterId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                error.setValue("Failed to post comment");
            }
        });
    }

    public void deleteComment(Long chapterId, Long commentId) {
        repository.deleteComment(chapterId, commentId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    fetchComments(chapterId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                error.setValue("Failed to delete comment");
            }
        });
    }
}