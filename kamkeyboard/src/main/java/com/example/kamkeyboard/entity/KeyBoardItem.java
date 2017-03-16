package com.example.kamkeyboard.entity;


import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Kam on 17/1/19.
 */

public class KeyBoardItem implements MultiItemEntity {

    public static final int NUMBER = 0;
    public static final int ALPHABET_NORMAL = 1;
    public static final int ALPHABET_A = 2;
    public static final int NUM_IMG_BACK = 3;
    public static final int ALPHABET_IMG_BACK = 4;
    public static final int IMG_CAPITAL = 5;

    public static final int ALPHABET_NORMAL_SPAN_SIZE = 36;
    public static final int IMG_SPAN_SIZE = 54;
    public static final int NUM_SPAN_SIZE = 1;

    private String content;
    private int spanSize;
    private int itemType;

    public KeyBoardItem(int itemType, int spanSize, String content) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.content = content;
    }

    public KeyBoardItem(int itemType, int spanSize) {
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
