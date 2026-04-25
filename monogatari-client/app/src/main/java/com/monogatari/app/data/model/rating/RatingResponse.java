package com.monogatari.app.data.model.rating;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RatingResponse {
    private Long id;
    private Integer score;
    private String review;
    private String username;
    private String avatarUrl;
    private LocalDateTime createdAt;
}