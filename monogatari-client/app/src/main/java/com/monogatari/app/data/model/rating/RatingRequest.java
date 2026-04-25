package com.monogatari.app.data.model.rating;

import lombok.Data;

@Data
public class RatingRequest {
    private Integer score;
    
    private String review;
}