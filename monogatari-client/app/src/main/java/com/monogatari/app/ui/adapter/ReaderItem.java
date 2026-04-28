package com.monogatari.app.ui.adapter;

public class ReaderItem {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;

    private final int type;
    private final String content;

    public ReaderItem(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}