package com.gprod.mediaio.interfaces.services.story;

import com.gprod.mediaio.models.story.Story;

public interface GettingStoryByIdCallback {
    void onSuccess(Story story);
    void onFailure();
}
