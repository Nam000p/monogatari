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
    private final MutableLiveData<UserProfileResponse> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<UserProfileResponse> getUserProfile() { return userProfile; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadProfile() {
        isLoading.setValue(true);
        userRepository.getMyProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    userProfile.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load profile");
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }
}