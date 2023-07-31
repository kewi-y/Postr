package com.gprod.mediaio.models.post;

import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.models.post.Comment;

import java.util.ArrayList;

public abstract class Post {
    private String authorId,postId,description;
    private PostTypes postType;
    private ArrayList<Comment> comments;
    private ArrayList<String> likes;
    private ArrayList<String> stringTagList;
    public Post(String authorId, String postId, String description, ArrayList<String> likes, ArrayList<Comment> comments,ArrayList<String> stringTagList, PostTypes postType){
        this.authorId = authorId;
        this.description = description;
        this.likes= likes;
        this.comments = comments;
        this.postType = postType;
        this.postId = postId;
        this.stringTagList = stringTagList;
    }
    public void addLike(String userId){
        if(likes == null){
            likes = new ArrayList<>();
            likes.add(userId);
        }
        else {
            likes.add(userId);
        }
    }
    public void removeLike(String user){
        if(likes != null){
            likes.remove(user);
        }
    }

    public void addComment(Comment comment){
        if(comments == null){
            comments = new ArrayList<>();
            comments.add(comment);
        }
        else {
            comments.add(comment);
        }
    }
    public String getPostId() {return postId;}
    public PostTypes getPostType() {return postType;}
    public String getAuthorId() {
        return authorId;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {this.likes = likes;}

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getStringTagList() {
        return stringTagList;
    }
}
