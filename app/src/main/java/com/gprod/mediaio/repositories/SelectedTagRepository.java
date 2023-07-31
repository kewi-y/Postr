package com.gprod.mediaio.repositories;

import com.gprod.mediaio.models.post.Tag;
import com.gprod.mediaio.services.database.firebase.FirebaseDatabaseService;

public class SelectedTagRepository {
    private static SelectedTagRepository instance;
    private Tag tag;
    private FirebaseDatabaseService firebaseDatabaseService;
    public static SelectedTagRepository getInstance() {
        if(instance == null){
            instance = new SelectedTagRepository();
        }
        return instance;
    }
    private SelectedTagRepository(){
        firebaseDatabaseService = FirebaseDatabaseService.getInstance();
    }
    public void setSelectedTag(Tag tag){
        this.tag = tag;
    }
    public void clearSelectedTag(){
        tag = null;
    }
    public Tag getSelectedTag(){
        return tag;
    }
}
