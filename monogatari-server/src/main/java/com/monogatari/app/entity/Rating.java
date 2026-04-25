package com.monogatari.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "ratings", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "story_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "story_id", nullable = false)
	private Story story;
	
	@Column(nullable = false)
	private Integer score;
	
	@Column(name = "review_text", columnDefinition = "TEXT")
	private String reviewText;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Instant createdAt;
}