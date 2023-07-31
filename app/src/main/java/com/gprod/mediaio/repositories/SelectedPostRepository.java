package com.gprod.mediaio.repositories;

import com.gprod.mediaio.models.post.Comment;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.services.database.firebase.FirebaseDatabaseService;

public class SelectedPostRepository {
    private static SelectedPostRepository instance;
    private Post post;
    private FirebaseDatabaseService firebaseDatabaseService;
    public static SelectedPostRepository getInstance() {
        if(instance == null){
            instance = new SelectedPostRepository();
        }
        return instance;
    }
    private SelectedPostRepository(){
        firebaseDatabaseService = FirebaseDatabaseService.getInstance();
    }
    public void setSelectedPost(Post post){
        this.post = post;
    }
    public void clearSelectedPost(){
        post = null;
    }
    public Post getSelectedPost(){
        return post;
    }
}
