package com.monogatari.app.dto.rating;

import lombok.Data;

import java.time.Instant;

@Data
public class RatingResponse {
    private Long id;
    private Integer score;
    private String review;
    private String username;
    private String avatarUrl;
    private Instant createdAt;
}