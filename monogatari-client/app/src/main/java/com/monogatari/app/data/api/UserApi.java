package com.monogatari.app.data.api;

import com.monogatari.app.data.model.user.UserChangePasswordRequest;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.model.user.UserUpdateProfileRequest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {
    @GET("users/profile")
    Call<UserProfileResponse> getMyProfile();

    @PUT("users/profile")
    Call<UserProfileResponse> updateProfile(@Body UserUpdateProfileRequest request);

    @Multipart
    @POST("users/avatar")
    Call<UserProfileResponse> updateAvatar(@Part MultipartBody.Part file);

    @POST("users/change-password")
    Call<String> changePassword(@Body UserChangePasswordRequest request);

    @DELETE("users/me")
    Call<String> deleteMyAccount();
}