package com.gprod.mediaio.models.post;

import android.util.Log;

import com.gprod.mediaio.models.post.Post;

public class PostItem {
    private Post post;
    private String username,accountname;
    private String userProfilePhotoDownloadUri;
    private boolean onLike = false;
    private boolean onFavorites = false;
    public PostItem(Post post, String username,String accountname,String userProfilePhotoDownloadUri,boolean onLike,boolean onFavorites){
        this.post = post;
        Log.d("MY LOGS", post.getPostId());
        this.username = username;
        this.accountname = accountname;
        this.userProfilePhotoDownloadUri = userProfilePhotoDownloadUri;
        this.onLike = onLike;
        this.onFavorites = onFavorites;
    }

    public Post getPost() {
        Log.d("MY LOGS", post.toString());
        return post;
    }

    public boolean isOnLike() {
        return onLike;
    }

    public void setOnLike(boolean onLike) {
        this.onLike = onLike;
    }

    public String getUsername() {
        return username;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getUserProfilePhotoDownloadUri() {
        return userProfilePhotoDownloadUri;
    }

    public boolean isOnFavorites() {
        return onFavorites;
    }

    public void setOnFavorites(boolean onFavorites) {
        this.onFavorites = onFavorites;
    }
}
