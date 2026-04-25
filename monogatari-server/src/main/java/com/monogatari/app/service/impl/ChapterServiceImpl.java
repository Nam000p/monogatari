package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.chapter.ChapterRequest;
import com.monogatari.app.dto.chapter.ChapterResponse;
import com.monogatari.app.entity.*;
import com.monogatari.app.enums.SubscriptionStatus;
import com.monogatari.app.repository.ChapterRepository;
import com.monogatari.app.repository.StoryRepository;
import com.monogatari.app.repository.SubscriptionRepository;
import com.monogatari.app.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl extends BaseService implements ChapterService {
	private final StoryRepository storyRepository;
	
	private final ChapterRepository chapterRepository;

    private final SubscriptionRepository subscriptionRepository;
	
	private final NotificationService notificationService;
	
	private final StoryNotificationService storyNotificationService;
	
	private final UserService userService;
	
	@Override
	protected UserService getUserService() {
		return userService;
	}
	
	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public ChapterResponse getChapterDetails(Long chapterId) {
		Long currentUserId = getCurrentUser().getId();
		Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        
		if (Boolean.TRUE.equals(chapter.getIsPremium())) {
            validatePremiumAccess(currentUserId);
        }

        return mapToResponse(chapter);
	}
	
	@Override
    @Transactional(readOnly = true)
	@TrackExecutionTime
    public List<ChapterResponse> getChaptersByStoryId(Long storyId) {
		getCurrentUser();
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found with ID: " + storyId));

        List<Chapter> chapters = chapterRepository.findByStoryIdOrderByChapterNumberAsc(story.getId());

        return chapters.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

	@Override
	@Transactional
	public ChapterResponse createChapter(Long storyId, ChapterRequest request) {
		Story story = storyRepository.findById(storyId)
				.orElseThrow(() -> new EntityNotFoundException("Story not found!"));
		Chapter chapter = Chapter.builder()
				.story(story)
				.chapterNumber(request.getChapterNumber())
				.title(request.getTitle())
				.content(request.getContent())
				.isPremium(request.getIsPremium() != null ? request.getIsPremium() : true)
				.build();
		if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ChapterImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ChapterImage img = ChapterImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .orderNumber(i + 1)
                        .chapter(chapter)
                        .build();
                images.add(img);
            }
            chapter.setImages(images);
        }

        Chapter savedChapter = chapterRepository.save(chapter);
        notificationService.createNewChapterNotification(storyId, savedChapter.getId());
        
        story.setUpdatedAt(Instant.now());
        storyRepository.save(story);
        
        ChapterResponse response = mapToResponse(savedChapter);
        storyNotificationService.broadcastNewChapter(storyId, "NEW_CHAPTER", response);

        return response;
	}
	
	private void validatePremiumAccess(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
             .orElseThrow(() -> new AccessDeniedException("PREMIUM Plan is required!"));
		if (subscription.getStatus() != SubscriptionStatus.ACTIVE ||
			subscription.getCurrentPeriodEnd() == null ||
			subscription.getCurrentPeriodEnd().isBefore(Instant.now())) {
			throw new AccessDeniedException("PREMIUM Plan is expired!");
		}
    }
	
	private ChapterResponse mapToResponse(Chapter chapter) {
        ChapterResponse response = new ChapterResponse();
        response.setId(chapter.getId());
        response.setChapterNumber(chapter.getChapterNumber());
        response.setTitle(chapter.getTitle());
        response.setIsPremium(chapter.getIsPremium());
        response.setContent(chapter.getContent());
        if (chapter.getImages() != null) {
            List<String> urls = chapter.getImages().stream()
                    .map(ChapterImage::getImageUrl)
                    .collect(Collectors.toList());
            response.setImageUrls(urls);
        }
        return response;
    }
}