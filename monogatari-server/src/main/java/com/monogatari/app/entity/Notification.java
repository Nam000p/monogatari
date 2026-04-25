package com.monogatari.app.entity;

import com.monogatari.app.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(length = 255, nullable = false)
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String message;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private NotificationType targetType;
	
	@Column(name = "target_id")
	private Long targetId;
	
	@Column(name = "is_read")
	@Builder.Default
	private Boolean isRead = false;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Instant createdAt;
}