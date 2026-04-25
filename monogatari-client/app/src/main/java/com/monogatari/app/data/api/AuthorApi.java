package com.monogatari.app.data.api;

import com.monogatari.app.data.model.author.AuthorResponse;
import com.monogatari.app.data.model.story.StoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AuthorApi {
    @GET("authors/{authorId}")
    Call<AuthorResponse> getAuthorDetails(@Path("authorId") Long authorId);

    @GET("authors/{authorId}/stories")
    Call<List<StoryResponse>> getStoriesByAuthor(@Path("authorId") Long authorId);
}