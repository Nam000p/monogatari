package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.CommentApi;
import com.monogatari.app.data.model.comment.CommentRequest;
import com.monogatari.app.data.model.comment.CommentResponse;

import java.util.List;

import retrofit2.Call;

public class CommentRepository {
    private final CommentApi commentApi;

    public CommentRepository(CommentApi commentApi) {
        this.commentApi = commentApi;
    }

    public Call<String> addComment(Long chapterId, CommentRequest request) {
        return commentApi.addComment(chapterId, request);
    }

    public Call<List<CommentResponse>> getComments(Long chapterId) {
        return commentApi.getComments(chapterId);
    }

    public Call<String> deleteComment(Long chapterId, Long commentId) {
        return commentApi.deleteComment(chapterId, commentId);
    }
}