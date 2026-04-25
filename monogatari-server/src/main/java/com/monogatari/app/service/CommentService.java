package com.monogatari.app.service;

import com.monogatari.app.dto.comment.CommentRequest;
import com.monogatari.app.dto.comment.CommentResponse;

import java.util.List;

public interface CommentService {
	void addComment(Long chapterId, CommentRequest request);
	
    List<CommentResponse> getCommentsByChapter(Long chapterId);

	void deleteComment(Long commentId);
}