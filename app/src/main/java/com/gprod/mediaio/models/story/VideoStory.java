package com.gprod.mediaio.models.story;

import com.gprod.mediaio.enums.story.StoryType;

import java.sql.Timestamp;

public class VideoStory extends Story{
    private String downloadVideoUri;
    public VideoStory(String id, String authorId, String downloadVideoUri, Timestamp timestamp) {
        super(id, StoryType.VIDEO_STORY, authorId, timestamp);
        this.downloadVideoUri = downloadVideoUri;
    }

    public String getDownloadVideoUri() {
        return downloadVideoUri;
    }
}
