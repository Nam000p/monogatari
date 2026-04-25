package com.monogatari.app.data.api;

import com.monogatari.app.data.model.genre.GenreResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GenreApi {
    @GET("genres")
    Call<List<GenreResponse>> getAllGenres();
}
