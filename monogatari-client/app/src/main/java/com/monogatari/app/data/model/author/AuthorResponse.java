package com.monogatari.app.data.model.author;

import lombok.Data;

@Data
public class AuthorResponse {
	private Long id;
	private String name;
	private String bio;
	private String avatarUrl;
}