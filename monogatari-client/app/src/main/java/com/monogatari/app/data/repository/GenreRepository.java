package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.GenreApi;
import com.monogatari.app.data.model.genre.GenreResponse;

import java.util.List;

import retrofit2.Call;

public class GenreRepository {
    private final GenreApi genreApi;

    public GenreRepository(GenreApi genreApi) {
        this.genreApi = genreApi;
    }

    public Call<List<GenreResponse>> getAllGenres() {
        return genreApi.getAllGenres();
    }
}