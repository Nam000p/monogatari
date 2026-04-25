package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.comment.CommentRequest;
import com.monogatari.app.dto.comment.CommentResponse;
import com.monogatari.app.entity.Chapter;
import com.monogatari.app.entity.Comment;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.SystemRole;
import com.monogatari.app.repository.ChapterRepository;
import com.monogatari.app.repository.CommentRepository;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.CommentService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends BaseService implements CommentService {
	private final CommentRepository commentRepository;
    
    private final ChapterRepository chapterRepository;
    
    private final UserService userService;
    
	@Override
	protected UserService getUserService() {
		return userService;
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 5, timeWindowMs = 60000)
	public void addComment(Long chapterId, CommentRequest request) {
		Chapter chapter = chapterRepository.findById(chapterId)
				.orElseThrow(() -> new EntityNotFoundException("Chapter not found!"));
		
        User currentUser = getCurrentUser();

        Comment comment = Comment.builder()
                .user(currentUser)
                .chapter(chapter)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public List<CommentResponse> getCommentsByChapter(Long chapterId) {
		getCurrentUser();
        return commentRepository.findByChapterIdOrderByCreatedAtDesc(chapterId).stream()
			.map((comment) -> {
				CommentResponse response = new CommentResponse();
				response.setId(comment.getId());
				response.setUserId(comment.getUser().getId());
				response.setUsername(comment.getUser().getUsername());
				response.setAvatarUrl(comment.getUser().getAvatarUrl());
				response.setContent(comment.getContent());
				response.setChapterId(comment.getChapter().getId());
				response.setCreatedAt(comment.getCreatedAt());
				return response;
			}).collect(Collectors.toList());
	}

	@Override
    @Transactional
    public void deleteComment(Long commentId) {
		User currentUser = getCurrentUser();
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new EntityNotFoundException("Comment not found!"));
		if (!comment.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(SystemRole.ROLE_ADMIN)) {
			throw new IllegalStateException("You don't have permission to delete this comment!");
		}
        commentRepository.deleteById(commentId);
    }
}