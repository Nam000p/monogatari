package com.monogatari.app.controller;

import com.monogatari.app.dto.comment.CommentRequest;
import com.monogatari.app.dto.comment.CommentResponse;
import com.monogatari.app.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters/{chapterId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<String> addComment(@PathVariable Long chapterId, @Valid @RequestBody CommentRequest request) {
        commentService.addComment(chapterId, request);
        return new ResponseEntity<>("Comment added successfully", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long chapterId) {
        List<CommentResponse> responses = commentService.getCommentsByChapter(chapterId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }
}