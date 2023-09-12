package com.gprod.mediaio.ui.fragments.qr.scan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

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
    public boolean checkPermissions(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            return false;
        }
    }
}