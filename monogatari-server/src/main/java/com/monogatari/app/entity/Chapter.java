package com.monogatari.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "chapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "story_id", nullable = false)
	private Story story;
	
	@Column(name = "chapter_number", nullable = false)
	private Float chapterNumber;
	
	@Column(length = 255)
	private String title;
	
	@Column(columnDefinition = "LONGTEXT")
	private String content;
	
	@OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChapterImage> images;
	
	@Column(name = "is_premium")
	@Builder.Default
	private Boolean isPremium = true;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Instant updatedAt;
}