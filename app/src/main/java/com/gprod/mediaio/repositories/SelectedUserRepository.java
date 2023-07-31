package com.gprod.mediaio.repositories;

import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.services.database.firebase.FirebaseDatabaseService;

public class SelectedUserRepository {
    private static SelectedUserRepository instance;
    private User selectedUser;
    private FirebaseDatabaseService firebaseDatabaseService;

    public static SelectedUserRepository getInstance() {
        if(instance == null){
            instance = new SelectedUserRepository();
        }
        return instance;
    }
    private SelectedUserRepository(){
        firebaseDatabaseService = FirebaseDatabaseService.getInstance();
    }
    public void setSelectedUser(User user){
        selectedUser = user;
    }
    public void clearSelectedUser(){
        selectedUser = null;
    }
    public User getSelectedUser(){
        return selectedUser;
    }
}
