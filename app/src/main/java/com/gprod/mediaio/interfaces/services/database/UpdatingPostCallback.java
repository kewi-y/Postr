package com.gprod.mediaio.interfaces.services.database;

import com.gprod.mediaio.models.post.Post;

public interface UpdatingPostCallback {
    void onSuccess(Post post);
    void onFailure();
}
