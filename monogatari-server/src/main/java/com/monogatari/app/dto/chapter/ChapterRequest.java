package com.monogatari.app.dto.chapter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ChapterRequest {
	@NotNull(message = "Chapter number is required")
    private Float chapterNumber;

    @NotBlank(message = "Title is required")
    private String title;

    private String content;
    private Boolean isPremium;
    private List<String> imageUrls;
}