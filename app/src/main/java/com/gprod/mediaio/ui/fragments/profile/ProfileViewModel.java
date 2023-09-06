package com.gprod.mediaio.ui.fragments.profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.core.ActivityScope;
import com.google.zxing.WriterException;
import com.gprod.mediaio.enums.profile.ProfileTypes;
import com.gprod.mediaio.interfaces.repositories.user.SubscribeCallback;
import com.gprod.mediaio.interfaces.repositories.user.UnsubscribeCallback;
import com.gprod.mediaio.interfaces.services.database.DeletingPostCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostListCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.interfaces.services.nfc.ShareNfcCallback;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.QrCodeRepository;
import com.gprod.mediaio.repositories.SelectedPostRepository;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;
import com.gprod.mediaio.services.nfc.NfcService;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private SelectedPostRepository selectedPostRepository;
    private SelectedUserRepository selectedUserRepository;
    private QrCodeRepository qrCodeRepository;
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Post>> postListLiveData = new MutableLiveData<>();
    private MutableLiveData<ProfileTypes> profileTypeLifeData = new MutableLiveData<>();

    public ProfileViewModel(){
        userRepository = UserRepository.getInstance();
        postRepository = PostRepository.getInstance();
        selectedPostRepository = SelectedPostRepository.getInstance();
        selectedUserRepository = SelectedUserRepository.getInstance();
        qrCodeRepository = QrCodeRepository.getInstance();
    }
    public LiveData<User> getUserLiveData(){
        return userLiveData;
    }
    public LiveData<ArrayList<Post>> getPostListLiveData(){return postListLiveData;}

    public LiveData<ProfileTypes> getProfileTypeLifeData() {return profileTypeLifeData;}

    public void loadProfile(Context context){
        LoadingPopup.show(context);
        if(selectedUserRepository.getSelectedUser() != null && !selectedUserRepository.getSelectedUser().getId().equals(userRepository.getUser().getId())){
            userLiveData.setValue(selectedUserRepository.getSelectedUser());
            profileTypeLifeData.setValue(ProfileTypes.OTHER_USER_PROFILE);
            postRepository.getPostListByAuthorId(selectedUserRepository.getSelectedUser().getId(), new GettingPostListCallback() {
                @Override
                public void onSuccess(ArrayList<Post> posts) {
                    LoadingPopup.hide(context);
                    if(postListLiveData.getValue() != null){
                        postListLiveData.getValue().clear();
                    }
                    postListLiveData.setValue(posts);
                }

                @Override
                public void onFailure() {
                    //TODO: Notify user
                }
            });
        }
        else {
            userRepository.updateActualUserInfo(new UpdatingUserCallback() {
                @Override
                public void onSuccess(User updatedUser) {
                    profileTypeLifeData.setValue(ProfileTypes.SELF_PROFILE);
                    userLiveData.setValue(updatedUser);
                    postRepository.getPostListByAuthorId(userRepository.getUser().getId(), new GettingPostListCallback() {
                        @Override
                        public void onSuccess(ArrayList<Post> posts) {
                            LoadingPopup.hide(context);
                            if(postListLiveData.getValue() != null) {
                                postListLiveData.getValue().clear();
                            }
                            postListLiveData.setValue(posts);
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }

                @Override
                public void onFailure(String textError) {

                }
            });
        }
    }
    public void clearSelectedUser(){
        selectedUserRepository.clearSelectedUser();
    }
    public void subscribe(SubscribeCallback subscribeCallback){
        if(userLiveData.getValue() != null && userLiveData.getValue() != userRepository.getUser()){
            userRepository.subscribeToUser(userLiveData.getValue(),subscribeCallback);
        }
    }
    public void unsubscribe(UnsubscribeCallback unsubscribeCallback){
        if(userLiveData.getValue() != null && userLiveData.getValue() != userRepository.getUser()){
            userRepository.unsubscribeToUser(userLiveData.getValue(),unsubscribeCallback);
        }
    }
    public boolean getSubscribeStatus(){
        if(userLiveData.getValue() != null) {
            if (userLiveData.getValue().getSubscribers() != null) {
                return userLiveData.getValue().getSubscribers().contains(userRepository.getUser().getId());
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    public void deletePost(Post post, DeletingPostCallback deletingPostCallback){
        postRepository.deletePost(post, new DeletingPostCallback() {
            @Override
            public void onSuccess() {
                postRepository.getPostListByAuthorId(userRepository.getUser().getId(), new GettingPostListCallback() {
                    @Override
                    public void onSuccess(ArrayList<Post> posts) {
                        postListLiveData.setValue(posts);
                        User updatableUser = userRepository.getUser();
                        if(posts != null) {
                            updatableUser.setPublicationsCount(posts.size());
                        }
                        else {
                            updatableUser.setPublicationsCount(0);
                        }
                        userRepository.updateUser(updatableUser, new UpdatingUserCallback() {
                            @Override
                            public void onSuccess(User updatedUser) {
                                userLiveData.setValue(updatedUser);
                                deletingPostCallback.onSuccess();
                            }

                            @Override
                            public void onFailure(String textError) {
                                deletingPostCallback.onFailure();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        deletingPostCallback.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                deletingPostCallback.onFailure();
            }
        });
    }
    public void setSelectedPost(Post post){
        selectedPostRepository.setSelectedPost(post);
    }
     public void reloadProfile(Context context){
        if(userLiveData.getValue() != null){
            if(selectedUserRepository.getSelectedUser() == null){
                selectedUserRepository.setSelectedUser(userLiveData.getValue());
                loadProfile(context);
            }
        }
     }
}