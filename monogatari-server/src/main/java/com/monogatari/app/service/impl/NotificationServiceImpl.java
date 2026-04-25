package com.monogatari.app.service.impl;

import com.monogatari.app.dto.notification.NotificationResponse;
import com.monogatari.app.entity.Follow;
import com.monogatari.app.entity.Notification;
import com.monogatari.app.entity.Story;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.NotificationType;
import com.monogatari.app.repository.FollowRepository;
import com.monogatari.app.repository.NotificationRepository;
import com.monogatari.app.repository.StoryRepository;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.NotificationService;
import com.monogatari.app.service.StoryNotificationService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends BaseService implements NotificationService {
	private final NotificationRepository notificationRepository;
	
	private final FollowRepository followRepository;
	
	private final StoryRepository storyRepository;
	
	private final StoryNotificationService storyNotificationService;
	
	private final UserService userService;
	
	@Override
	protected UserService getUserService() {
		return userService;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NotificationResponse> getUserNotifications() {
		User currentUser = getCurrentUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void markAsRead(Long notificationId) {
		User currentUser = getCurrentUser();
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new EntityNotFoundException("Notification not found!"));
		if (!notification.getUser().getId().equals(currentUser.getId())) {
			throw new IllegalArgumentException("You don't have permission to modify this notification");
		}
		notification.setIsRead(true);
		notificationRepository.save(notification);
	}

	@Override
	@Transactional
	public void createNewChapterNotification(Long storyId, Long chapterId) {
		Story story = storyRepository.findById(storyId)
				.orElseThrow(() -> new EntityNotFoundException("Entity not found!"));
		List<Follow> followers = followRepository.findByStoryId(story.getId());
		String title = "New Chapter Alert!";
        String message = "A new chapter has been released for '" + story.getTitle() + "'. Read it now!";
        for (Follow follow : followers) {
            createNotification(
                follow.getUser(),
                title,
                message,
					chapterId
            );
            
            storyNotificationService.sendPrivateNotification(
                    follow.getUser().getId(), 
                    "NEW_CHAPTER_NOTIFICATION", 
                    message
                );
        }
	}
	
    private void createNotification(User user, String title, String message, Long targetId) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .targetType(NotificationType.CHAPTER)
                .targetId(targetId)
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
    }
	
	private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setTargetType(notification.getTargetType());
        response.setTargetId(notification.getTargetId());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}