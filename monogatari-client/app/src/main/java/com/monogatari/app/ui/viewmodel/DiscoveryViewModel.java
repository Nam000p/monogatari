package com.monogatari.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.monogatari.app.data.model.author.AuthorResponse;
import com.monogatari.app.data.model.genre.GenreResponse;
import com.monogatari.app.data.repository.AuthorRepository;
import com.monogatari.app.data.repository.GenreRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoveryViewModel extends ViewModel {
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;

    private final MutableLiveData<List<GenreResponse>> genres = new MutableLiveData<>();
    private final MutableLiveData<AuthorResponse> authorDetail = new MutableLiveData<>();

    public DiscoveryViewModel(GenreRepository genreRepo, AuthorRepository authorRepo) {
        this.genreRepository = genreRepo;
        this.authorRepository = authorRepo;
    }

    public LiveData<List<GenreResponse>> getGenres() { return genres; }
    public LiveData<AuthorResponse> getAuthorDetail() { return authorDetail; }

    public void loadGenres() {
        genreRepository.getAllGenres().enqueue(new Callback<List<GenreResponse>>() {
            @Override
            public void onResponse(Call<List<GenreResponse>> call, Response<List<GenreResponse>> response) {
                if (response.isSuccessful()) genres.setValue(response.body());
            }
            @Override
            public void onFailure(Call<List<GenreResponse>> call, Throwable t) {}
        });
    }

    public void loadAuthor(Long authorId) {
        authorRepository.getAuthorDetails(authorId).enqueue(new Callback<AuthorResponse>() {
            @Override
            public void onResponse(Call<AuthorResponse> call, Response<AuthorResponse> response) {
                if (response.isSuccessful()) authorDetail.setValue(response.body());
            }
            @Override
            public void onFailure(Call<AuthorResponse> call, Throwable t) {}
        });
    }
}