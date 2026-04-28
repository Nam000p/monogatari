package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.model.user.UserChangePasswordRequest;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.model.user.UserUpdateProfileRequest;
import okhttp3.MultipartBody;
import retrofit2.Call;

public class UserRepository {
    private final UserApi userApi;

    public UserRepository(UserApi userApi) {
        this.userApi = userApi;
    }

    public Call<UserProfileResponse> getMyProfile() {
        return userApi.getMyProfile();
    }

    public Call<UserProfileResponse> updateProfile(UserUpdateProfileRequest request) {
        return userApi.updateProfile(request);
    }

    public Call<UserProfileResponse> updateAvatar(MultipartBody.Part file) {
        return userApi.updateAvatar(file);
    }

    public Call<String> changePassword(UserChangePasswordRequest request) {
        return userApi.changePassword(request);
    }

    public Call<String> deleteMyAccount() {
        return userApi.deleteMyAccount();
    }
}
