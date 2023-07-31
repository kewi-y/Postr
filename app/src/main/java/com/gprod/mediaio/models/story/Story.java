package com.gprod.mediaio.models.story;

import com.gprod.mediaio.enums.story.StoryType;

import java.sql.Timestamp;

public abstract class Story {
    private String id,authorId;
    private Timestamp timestamp;
    private StoryType storyType;
    public Story(String id,StoryType storyType,String authorId,Timestamp timestamp){
        this.id = id;
        this.authorId = authorId;
        this.storyType = storyType;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public StoryType getStoryType() {
        return storyType;
    }
}
