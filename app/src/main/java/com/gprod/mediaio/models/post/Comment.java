package com.gprod.mediaio.models.post;

import com.gprod.mediaio.models.user.User;

public class Comment {
    private String authorId,authorName,id,authorProfilePhotoDownloadUri;
    private String content;
    public Comment(User author, String id, String content){
        this.authorId = author.getId();
        this.authorProfilePhotoDownloadUri = author.getProfilePhotoDownloadUri();
        this.content = content;
        this.authorName = author.getUsername();
        this.id = id;
    }
    public Comment(String id,String authorId,String authorName,String authorProfilePhotoDownloadUri,String content){
        this.authorId = authorId;
        this.authorProfilePhotoDownloadUri = authorProfilePhotoDownloadUri;
        this.content = content;
        this.authorName = authorName;
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }
    public String getAuthorName() { return authorName; }
    public String getId() { return id; }
    public String getAuthorProfilePhotoDownloadUri() { return authorProfilePhotoDownloadUri; }
    public String getContent() {
        return content;
    }
}
