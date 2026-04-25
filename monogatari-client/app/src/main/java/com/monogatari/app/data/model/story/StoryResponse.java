package com.monogatari.app.data.model.story;

import com.monogatari.app.data.model.enums.StoryStatus;
import com.monogatari.app.data.model.enums.StoryType;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StoryResponse implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String authorName;
    private StoryType type;
    private StoryStatus status;
    private Double averageRating;
    private List<String> genres;
}