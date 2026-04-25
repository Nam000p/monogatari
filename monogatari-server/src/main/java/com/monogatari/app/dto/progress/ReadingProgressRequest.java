package com.monogatari.app.dto.progress;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReadingProgressRequest {
	@NotNull(message = "Story ID is required")
    private Long storyId;
    
    @NotNull(message = "Chapter ID is required")
    private Long chapterId;
    
    @NotNull(message = "Last page is required")
    @Min(value = 0, message = "Last page cannot be negative")
    private Integer lastPage;
}