package com.monogatari.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "reading_progress", uniqueConstraints = {
		@UniqueConstraint(name = "idx_user_story_progress", columnNames = {"user_id", "story_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingProgress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "story_id", nullable = false)
	private Story story;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chapter_id", nullable = false)
	private Chapter chapter;
	
	@Column(name = "last_read_page")
	@Builder.Default
	private Integer lastReadPage = 0;

	@Column(name = "last_read_at", insertable = false, updatable = false)
	private Instant lastReadAt;
}