package com.monogatari.app.data.model.progress;

import lombok.Data;

@Data
public class ReadingProgressRequest {
    private Long storyId;
    private Long chapterId;
    private Integer lastPage;
}