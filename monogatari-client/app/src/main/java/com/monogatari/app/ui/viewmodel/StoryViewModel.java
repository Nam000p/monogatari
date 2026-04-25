package com.monogatari.app.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.model.ai.AiResponse;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.data.model.common.PageResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
public class StoryViewModel extends ViewModel {
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final AiRepository aiRepository;
    private final PaymentRepository paymentRepository;

    private final MutableLiveData<List<StoryResponse>> stories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<StoryResponse> storyDetail = new MutableLiveData<>();
    private final MutableLiveData<List<ChapterResponse>> chapters = new MutableLiveData<>();
    private final MutableLiveData<ChapterResponse> chapterDetail = new MutableLiveData<>();
    private final MutableLiveData<AiResponse> aiResponse = new MutableLiveData<>();
    private final MutableLiveData<String> paymentUrl = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private int currentPage = 0;
    private boolean isLastPage = false;

    public StoryViewModel(StoryRepository storyRepository, ChapterRepository chapterRepository,
                          CommentRepository commentRepo, RatingRepository ratingRepo,
                          ReadingProgressRepository progressRepo, GenreRepository genreRepo,
                          AuthorRepository authorRepo, AiRepository aiRepository,
                          PaymentRepository paymentRepository) {
        this.storyRepository = storyRepository;
        this.chapterRepository = chapterRepository;
        this.aiRepository = aiRepository;
        this.paymentRepository = paymentRepository;
    }

    public void loadStories(String search, boolean isRefresh) {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;
        if (isLastPage && !isRefresh) return;
        if (isRefresh) { currentPage = 0; isLastPage = false; }

        isLoading.setValue(true);
        storyRepository.getStories(search, currentPage, 10).enqueue(new Callback<PageResponse<StoryResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<StoryResponse>> call, Response<PageResponse<StoryResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<StoryResponse> newData = response.body().getContent();
                    if (isRefresh) stories.setValue(new ArrayList<>(newData));
                    else {
                        List<StoryResponse> current = stories.getValue();
                        if (current != null) { current.addAll(newData); stories.setValue(current); }
                    }
                    isLastPage = currentPage >= response.body().getTotalPages() - 1;
                    if (!isLastPage) currentPage++;
                }
            }
            @Override public void onFailure(Call<PageResponse<StoryResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void loadStoryDetails(long storyId) {
        isLoading.setValue(true);
        storyRepository.getStoryDetails(storyId).enqueue(new Callback<StoryResponse>() {
            @Override
            public void onResponse(Call<StoryResponse> call, Response<StoryResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) storyDetail.setValue(response.body());
                else errorMessage.setValue("Story not found: " + response.code());
            }
            @Override public void onFailure(Call<StoryResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void loadChapters(long storyId) {
        isLoading.setValue(true);
        storyRepository.getChapters(storyId).enqueue(new Callback<List<ChapterResponse>>() {
            @Override
            public void onResponse(Call<List<ChapterResponse>> call, Response<List<ChapterResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    chapters.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load chapters: " + response.code());
                }
            }
            @Override public void onFailure(Call<List<ChapterResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void loadChapterDetail(long storyId, long currentChapterId) {
        isLoading.setValue(true);
        chapterRepository.getChapter(storyId, currentChapterId).enqueue(new Callback<ChapterResponse>() {
            @Override
            public void onResponse(Call<ChapterResponse> call, Response<ChapterResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    chapterDetail.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load chapter: " + response.code());
                }
            }
            @Override public void onFailure(Call<ChapterResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void chatWithAi(String message) {
        isLoading.setValue(true);
        aiRepository.chatWithAi(message).enqueue(new Callback<AiResponse>() {
            @Override
            public void onResponse(Call<AiResponse> call, Response<AiResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) aiResponse.setValue(response.body());
                else errorMessage.setValue("AI Error: " + response.code());
            }
            @Override public void onFailure(Call<AiResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void createPayment() {
        isLoading.setValue(true);
        paymentRepository.createCheckout().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String url = response.body().get("url");
                    if (url != null) paymentUrl.setValue(url);
                } else {
                    isLoading.setValue(false);
                    errorMessage.setValue("Payment session failed: " + response.code());
                }
            }
            @Override public void onFailure(Call<Map<String, String>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }
}