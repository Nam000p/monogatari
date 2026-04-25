package com.monogatari.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "follows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {
	@EmbeddedId
	private FollowId id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("storyId")
	@JoinColumn(name = "story_id")
	private Story story;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Instant createdAt;
}