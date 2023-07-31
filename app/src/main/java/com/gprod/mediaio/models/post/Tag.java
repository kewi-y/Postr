package com.gprod.mediaio.models.post;

import android.content.Intent;

public class Tag {
    private String stringTag;
    private long viewCount;
    public Tag(String tag){
        this.stringTag = tag;
        viewCount = 0;
    }
    public Tag(String tag,Long viewCount){
        this.stringTag = tag;
        this.viewCount = viewCount;
    }

    public String getStringTag() {
        return stringTag;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void addViewCount() {
        viewCount+=1;
    }
    public void deleteViewCount(){
        viewCount-=1;
    }
}
