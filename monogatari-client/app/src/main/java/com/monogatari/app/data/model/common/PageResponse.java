package com.monogatari.app.data.model.common;

import java.util.List;

import lombok.Data;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;
}