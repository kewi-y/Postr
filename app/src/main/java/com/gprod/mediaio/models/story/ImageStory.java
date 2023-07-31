package com.gprod.mediaio.models.story;

import com.gprod.mediaio.enums.story.StoryType;

import java.sql.Timestamp;

public class ImageStory extends Story{
    private String downloadImageUri;
    public ImageStory(String id,String authorId, String downloadImageUri, Timestamp timestamp) {
        super(id, StoryType.IMAGE_STORY, authorId, timestamp);
        this.downloadImageUri = downloadImageUri;
    }

    public String getDownloadImageUri() {
        return downloadImageUri;
    }
}
