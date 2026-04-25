package com.monogatari.app.entity;

import jakarta.persistence.*;
import lombok.*;

import com.monogatari.app.enums.StoryStatus;
import com.monogatari.app.enums.StoryType;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "stories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 255)
	private String title;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private Author author;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "cover_url")
	private String coverUrl;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StoryType type;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private StoryStatus status = StoryStatus.ONGOING;

	@Column(name = "age_limit")
    @Builder.Default
    private Integer ageLimit = 0;
	
	@Column(name = "view_count")
	@Builder.Default
	private Long viewCount = 0L;
	
	@Column(name = "average_rating")
	@Builder.Default
	private Double averageRating = 0.0;
	
	@ManyToMany
	@JoinTable(
		name = "story_genre",
		joinColumns = @JoinColumn(name = "story_id"),
		inverseJoinColumns = @JoinColumn(name = "genre_id")
	)	
	private Set<Genre> genre;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Instant createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Instant updatedAt;
}