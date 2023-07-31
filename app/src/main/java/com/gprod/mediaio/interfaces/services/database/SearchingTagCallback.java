package com.gprod.mediaio.interfaces.services.database;

import com.gprod.mediaio.models.post.Tag;

import java.util.ArrayList;

public interface SearchingTagCallback {
    void onResult(ArrayList<Tag> tagList);
}
