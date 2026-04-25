package com.monogatari.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.monogatari.app.data.model.comment.CommentRequest;
import com.monogatari.app.data.model.comment.CommentResponse;
import com.monogatari.app.data.model.rating.RatingRequest;
import com.monogatari.app.data.model.rating.RatingResponse;
import com.monogatari.app.data.repository.CommentRepository;
import com.monogatari.app.data.repository.RatingRepository;
import com.monogatari.app.data.repository.ReadingProgressRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialViewModel extends ViewModel {
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final ReadingProgressRepository progressRepository;

    private final MutableLiveData<List<CommentResponse>> comments = new MutableLiveData<>();
    private final MutableLiveData<List<RatingResponse>> ratings = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public SocialViewModel(CommentRepository commentRepo, RatingRepository ratingRepo, ReadingProgressRepository progressRepo) {
        this.commentRepository = commentRepo;
        this.ratingRepository = ratingRepo;
        this.progressRepository = progressRepo;
    }

    public LiveData<List<CommentResponse>> getComments() { return comments; }
    public LiveData<List<RatingResponse>> getRatings() { return ratings; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void loadComments(Long chapterId) {
        commentRepository.getComments(chapterId).enqueue(new Callback<List<CommentResponse>>() {
            @Override
            public void onResponse(Call<List<CommentResponse>> call, Response<List<CommentResponse>> response) {
                if (response.isSuccessful()) comments.setValue(response.body());
            }
            @Override
            public void onFailure(Call<List<CommentResponse>> call, Throwable t) {}
        });
    }

    public void postComment(Long chapterId, CommentRequest request) {
        commentRepository.addComment(chapterId, request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    statusMessage.setValue("Comment added!");
                    loadComments(chapterId); // Refresh
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });
    }

    public void submitRating(Long storyId, RatingRequest request) {
        ratingRepository.rateStory(storyId, request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) statusMessage.setValue("Rating submitted!");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });
    }
}