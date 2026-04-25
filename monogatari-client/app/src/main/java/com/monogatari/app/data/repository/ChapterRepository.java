package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.ChapterApi;
import com.monogatari.app.data.model.chapter.ChapterResponse;

import retrofit2.Call;

public class ChapterRepository {
    private final ChapterApi chapterApi;
    public ChapterRepository(ChapterApi chapterApi) {
        this.chapterApi = chapterApi;
    }
    public Call<ChapterResponse> getChapter(Long storyId, Long chapterId) {
        return chapterApi.getChapter(storyId, chapterId);
    }
}