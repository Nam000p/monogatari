package com.monogatari.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(columnDefinition = "TEXT")
	private String bio;
	
	@Column(length = 255, name = "avatar_url")
	private String avatarUrl;
	
	@OneToMany(mappedBy = "author")
	private List<Story> stories;
}