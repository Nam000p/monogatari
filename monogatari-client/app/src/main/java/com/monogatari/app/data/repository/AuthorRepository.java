package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.AuthorApi;
import com.monogatari.app.data.model.author.AuthorResponse;
import com.monogatari.app.data.model.story.StoryResponse;

import java.util.List;

import retrofit2.Call;

public class AuthorRepository {
    private final AuthorApi authorApi;

    public AuthorRepository(AuthorApi authorApi) {
        this.authorApi = authorApi;
    }

    public Call<AuthorResponse> getAuthorDetails(Long authorId) {
        return authorApi.getAuthorDetails(authorId);
    }

    public Call<List<StoryResponse>> getStoriesByAuthor(Long authorId) {
        return authorApi.getStoriesByAuthor(authorId);
    }
}
