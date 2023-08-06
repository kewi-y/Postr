package com.gprod.mediaio.models.story;

import java.util.ArrayList;

public class StoryItem {
    private ArrayList<Story> stories;
    private String username,accountname;
    private String userProfilePhotoDownloadUri;
    public StoryItem(ArrayList<Story>stories,String username,String accountname,String userProfilePhotoDownloadUri){
        this.stories = stories;
        this.username = username;
        this.accountname = accountname;
        this.userProfilePhotoDownloadUri = userProfilePhotoDownloadUri;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public String getUsername() {
        return username;
    }

    public String getAccountname() {
        return accountname;
    }

    public String getUserProfilePhotoDownloadUri() {
        return userProfilePhotoDownloadUri;
    }
}
