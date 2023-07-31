package com.gprod.mediaio.interfaces.dialogs.album;

import com.gprod.mediaio.models.post.Post;

import java.util.ArrayList;

public interface AddPostsToAlbumCallback {
    void onAdd(ArrayList<Post> postList);
}
