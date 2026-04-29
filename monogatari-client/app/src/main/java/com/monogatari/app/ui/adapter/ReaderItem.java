package com.monogatari.app.ui.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReaderItem {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;

    private final int type;
    private final String content;
}