package com.monogatari.app.dto.chapter;

import lombok.Data;

import java.util.List;

@Data
public class ChapterResponse {
    private Long id;
    private Float chapterNumber;
    private String title;
    private Boolean isPremium;
    private String content; 
    private List<String> imageUrls; 
}