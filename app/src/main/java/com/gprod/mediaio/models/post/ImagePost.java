package com.gprod.mediaio.models.post;

import com.gprod.mediaio.enums.post.PostTypes;

import java.util.ArrayList;

public class ImagePost extends Post {
    private ArrayList<String> postImageDownloadUriList;
    public ImagePost(String authorId, String postId, ArrayList<String> postImageDownloadUriList, String description,
                     ArrayList<String> likes, ArrayList<Comment> comments, ArrayList<String> stringTagList) {
        super(authorId, postId,description, likes, comments,stringTagList,PostTypes.IMAGE_POST);
        this.postImageDownloadUriList = postImageDownloadUriList;
    }
    public ArrayList<String> getPostImageDownloadUriList() {
        return postImageDownloadUriList;
    }
}
