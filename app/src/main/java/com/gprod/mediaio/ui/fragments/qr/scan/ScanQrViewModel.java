package com.gprod.mediaio.ui.fragments.qr.scan;

import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;

public class ScanQrViewModel extends ViewModel {
    private SelectedUserRepository selectedUserRepository;
    private UserRepository userRepository;
    public ScanQrViewModel(){
        selectedUserRepository = SelectedUserRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }
    public void selectUser(String userId,SelectUserCallback selectUserCallback){
        userRepository.getUserByID(userId, new GettingUserByIdCallback() {
            @Override
            public void onSuccess(User user) {
                selectedUserRepository.setSelectedUser(user);
                selectUserCallback.onSelected();
            }

            @Override
            public void onFailure() {
                selectUserCallback.onFailure();
            }
        });
    }
}