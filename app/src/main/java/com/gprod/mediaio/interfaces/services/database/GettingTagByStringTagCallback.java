package com.gprod.mediaio.interfaces.services.database;

import com.gprod.mediaio.models.post.Tag;

public interface GettingTagByStringTagCallback {
    void onResult(Tag tag);
    void onFailure();
}
