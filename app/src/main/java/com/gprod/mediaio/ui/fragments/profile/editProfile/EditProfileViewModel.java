package com.gprod.mediaio.ui.fragments.profile.editProfile;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.services.database.CheckingForUniqueCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.interfaces.services.storage.UploadFileCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.TempPhotoRepository;
import com.gprod.mediaio.repositories.UserRepository;

public class EditProfileViewModel extends ViewModel {
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private UserRepository userRepository;
    private TempPhotoRepository tempPhotoRepository;
    private MutableLiveData<Uri> tempImageUriLiveData = new MutableLiveData<>();
    public EditProfileViewModel(){
        userRepository = UserRepository.getInstance();
        tempPhotoRepository = TempPhotoRepository.getInstance();
    }
    public LiveData<User> getUserLiveData(){
        return userLiveData;
    }
    public LiveData<Uri> getTempImageUriLiveData(){
        return tempImageUriLiveData;
    }
    public void loadData(Context context){
        userLiveData.setValue(userRepository.getUser());
        if(tempPhotoRepository.getTempImageBitmap() != null){
            tempImageUriLiveData.setValue(tempPhotoRepository.getTempImageUri());
        }
    }
    public void updateUserInfo(String username, String profilename, String bio, UpdatingUserCallback updatingUserCallback){
        if(!userRepository.getUser().getProfilename().equals(profilename)) {
            userRepository.checkProfilenameForUnique(profilename, new CheckingForUniqueCallback() {
                @Override
                public void isUnique() {
                    updateUser(username, profilename, bio, updatingUserCallback);
                }

                @Override
                public void isNotUnique() {
                    updatingUserCallback.onFailure("Этот тэг уже занят");
                    //TODO: migrate error text to resources
                }
            });
        }
        else {
            updateUser(username, profilename, bio, new UpdatingUserCallback() {
                @Override
                public void onSuccess(User updatedUser) {
                    updatingUserCallback.onSuccess(updatedUser);
                }

                @Override
                public void onFailure(String textError) {
                    updatingUserCallback.onFailure(textError);
                }
            });
        }
     }


     private void updateUser(String username, String profilename, String bio, UpdatingUserCallback updatingUserCallback){
         User editableUserInfo = userRepository.getUser();
         editableUserInfo.setUsername(username);
         editableUserInfo.setBio(bio);
         editableUserInfo.setProfilename(profilename);
         userRepository.updateUser(editableUserInfo, new UpdatingUserCallback() {
             @Override
             public void onSuccess(User updatedUser) {
                 if(tempPhotoRepository.getTempImageBitmap() != null){
                     userRepository.uploadProfilePhoto(tempPhotoRepository.getTempImageBitmap(), new UploadFileCallback() {
                         @Override
                         public void onSuccess(Uri downloadUri) {
                             updatingUserCallback.onSuccess(updatedUser);
                             tempPhotoRepository.clearTempImage();
                         }

                         @Override
                         public void onProgress(int percent) {

                         }

                         @Override
                         public void onFailure() {

                         }
                     });
                 }
                 else{
                     updatingUserCallback.onSuccess(updatedUser);
                 }
             }
             @Override
             public void onFailure(String textError) {
                 updatingUserCallback.onFailure(textError);
             }
         });
     }
     public void exitFromAccount(){
        userRepository.exitFormAccount();
     }
}