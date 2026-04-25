package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.monogatari.app.data.repository.*;

public class StoryViewModelFactory implements ViewModelProvider.Factory {
    private final StoryRepository storyRepo;
    private final ChapterRepository chapterRepo;
    private final CommentRepository commentRepo;
    private final RatingRepository ratingRepo;
    private final ReadingProgressRepository progressRepo;
    private final GenreRepository genreRepo;
    private final AuthorRepository authorRepo;
    private final AiRepository aiRepo;
    private final PaymentRepository paymentRepo;

    public StoryViewModelFactory(StoryRepository storyRepo, ChapterRepository chapterRepo,
                                 CommentRepository commentRepo, RatingRepository ratingRepo,
                                 ReadingProgressRepository progressRepo, GenreRepository genreRepo,
                                 AuthorRepository authorRepo, AiRepository aiRepo,
                                 PaymentRepository paymentRepo) {
        this.storyRepo = storyRepo;
        this.chapterRepo = chapterRepo;
        this.commentRepo = commentRepo;
        this.ratingRepo = ratingRepo;
        this.progressRepo = progressRepo;
        this.genreRepo = genreRepo;
        this.authorRepo = authorRepo;
        this.aiRepo = aiRepo;
        this.paymentRepo = paymentRepo;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StoryViewModel.class)) {
            return (T) new StoryViewModel(storyRepo, chapterRepo, commentRepo, ratingRepo,
                    progressRepo, genreRepo, authorRepo, aiRepo, paymentRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}