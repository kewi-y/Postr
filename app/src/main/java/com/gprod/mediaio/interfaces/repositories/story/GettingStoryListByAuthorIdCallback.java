package com.gprod.mediaio.interfaces.repositories.story;

import com.gprod.mediaio.models.story.Story;

import java.util.ArrayList;

public interface GettingStoryListByAuthorIdCallback {
    void onSuccess(ArrayList<Story> storyList);
    void onFailure();
}
