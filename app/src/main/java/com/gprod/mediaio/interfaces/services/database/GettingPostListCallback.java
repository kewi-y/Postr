package com.gprod.mediaio.interfaces.services.database;

import com.gprod.mediaio.models.post.Post;

import java.util.ArrayList;

public interface GettingPostListCallback {
    void onSuccess(ArrayList<Post> posts);
    void onFailure();
}
