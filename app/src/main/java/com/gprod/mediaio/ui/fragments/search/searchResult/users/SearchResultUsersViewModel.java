package com.gprod.mediaio.ui.fragments.search.searchResult.users;

import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.SelectedUserRepository;

public class SearchResultUsersViewModel extends ViewModel {
    private SelectedUserRepository selectedUserRepository;
    public SearchResultUsersViewModel(){
        selectedUserRepository = SelectedUserRepository.getInstance();
    }
    public void selectUser(User user){
        selectedUserRepository.setSelectedUser(user);
    }

}