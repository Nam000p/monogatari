package com.monogatari.app.dto.story;

import com.monogatari.app.enums.StoryStatus;
import com.monogatari.app.enums.StoryType;
import lombok.Data;

import java.util.List;

@Data
public class StoryResponse {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String authorName;
    private StoryType type;
    private StoryStatus status;
    private Integer ageLimit;
    private Double averageRating;
    private List<String> genres;
}