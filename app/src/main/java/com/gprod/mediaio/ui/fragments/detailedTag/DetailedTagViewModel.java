package com.gprod.mediaio.ui.fragments.detailedTag;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostListCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.SelectedTagRepository;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;

import java.util.ArrayList;

public class DetailedTagViewModel extends ViewModel {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private SelectedTagRepository selectedTagRepository;
    private SelectedUserRepository selectedUserRepository;
    private MutableLiveData<ArrayList<PostItem>>postItemListLiveData = new MutableLiveData<>();
    public DetailedTagViewModel(){
        postRepository = PostRepository.getInstance();
        userRepository = UserRepository.getInstance();
        selectedTagRepository = SelectedTagRepository.getInstance();
        selectedUserRepository = SelectedUserRepository.getInstance();
    }

    public MutableLiveData<ArrayList<PostItem>> getPostItemListLiveData() {
        return postItemListLiveData;
    }

    private boolean checkOnLike(Post post){
        if(post.getLikes() != null){
            return post.getLikes().contains(userRepository.getUser().getId());
        }
        else {
            return false;
        }
    }
    private boolean checkOnFavorites(Post post){
        if(userRepository.getUser().getFavoritesPostList() != null){
            boolean isOnFavorites = userRepository.getUser().getFavoritesPostList().contains(post.getPostId());
            Log.d("MY LOGS", "on favorites >>: " + isOnFavorites);
            return isOnFavorites;
        }
        else {
            return false;
        }
    }
    public void addPostToFavorites(Post post, UpdatingUserCallback updatingUserCallback){
        userRepository.addPostToFavorites(post,updatingUserCallback);
    }
    public void removePostFromFavorites(Post post,UpdatingUserCallback updatingUserCallback){
        userRepository.removePostFromFavorites(post,updatingUserCallback);
    }
    public void addLike(Post post, UpdatingPostCallback updatingPostCallback){
        postRepository.addLike(post,userRepository.getUser(),updatingPostCallback);
    }
    public void removeLike(Post post,UpdatingPostCallback updatingPostCallback){
        if(checkOnLike(post)){
            postRepository.removeLike(post,userRepository.getUser(),updatingPostCallback);
        }
    }
    public void addComment(Post post,String commentContent,UpdatingPostCallback updatingPostCallback){
        postRepository.addComment(post,userRepository.getUser(),commentContent,updatingPostCallback);
    }
    public void loadPostList(){
        postRepository.getPostListByTag(selectedTagRepository.getSelectedTag().getStringTag(), new GettingPostListCallback() {
            @Override
            public void onSuccess(ArrayList<Post> posts) {
                ArrayList<PostItem> postItemList = new ArrayList<>();
                for(Post post : posts){
                    userRepository.getUserByID(post.getAuthorId(), new GettingUserByIdCallback() {
                        @Override
                        public void onSuccess(User user) {
                            postItemList.add(new PostItem(post,user.getUsername(),user.getProfilename(),
                                    user.getProfilePhotoDownloadUri(),checkOnLike(post),checkOnFavorites(post)));
                            postItemListLiveData.setValue(postItemList);
                        }

                        @Override
                        public void onFailure() {
                            //TODO: Notify user
                        }
                    });
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }
    public void selectUser(String userId, SelectUserCallback selectUserCallback){
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