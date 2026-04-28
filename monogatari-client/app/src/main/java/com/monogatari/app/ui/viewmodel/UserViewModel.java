package com.monogatari.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<UserProfileResponse> profile = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<UserProfileResponse> getProfile() { return profile; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void fetchProfile() {
        isLoading.postValue(true);
        userRepository.getMyProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    profile.postValue(response.body());
                } else {
                    error.postValue("Request user profile failed!");
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                isLoading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }
}