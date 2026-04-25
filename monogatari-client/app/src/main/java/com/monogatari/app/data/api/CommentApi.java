package com.monogatari.app.data.api;

import com.monogatari.app.data.model.comment.CommentRequest;
import com.monogatari.app.data.model.comment.CommentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentApi {
    @POST("chapters/{chapterId}/comments")
    Call<String> addComment(
            @Path("chapterId") Long chapterId,
            @Body CommentRequest request
    );

    @GET("chapters/{chapterId}/comments")
    Call<List<CommentResponse>> getComments(@Path("chapterId") Long chapterId);

    @DELETE("chapters/{chapterId}/comments/{commentId}")
    Call<String> deleteComment(
            @Path("chapterId") Long chapterId,
            @Path("commentId") Long commentId
    );
}