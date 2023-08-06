package com.gprod.mediaio.ui.fragments.favorites;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostByIdCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.album.Album;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.SelectedAlbumRepository;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;

import java.util.ArrayList;
import java.util.Collections;

public class FavoritesViewModel extends ViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private SelectedAlbumRepository selectedAlbumRepository;
    private SelectedUserRepository selectedUserRepository;
    private MutableLiveData<ArrayList<PostItem>> favoritesPostItemListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Album>> albumListLiveData = new MutableLiveData<>();
    public FavoritesViewModel(){
        userRepository = UserRepository.getInstance();
        postRepository = PostRepository.getInstance();
        selectedAlbumRepository = SelectedAlbumRepository.getInstance();
        selectedUserRepository = SelectedUserRepository.getInstance();
    }

    public LiveData<ArrayList<PostItem>> getFavoritesPostItemListLiveData() {
        return favoritesPostItemListLiveData;
    }

    public MutableLiveData<ArrayList<Album>> getAlbumListLiveData() {
        return albumListLiveData;
    }

    private boolean checkOnLike(Post post){
        if(post.getLikes() != null){
            return post.getLikes().contains(userRepository.getUser().getId());
        }
        else {
            return false;
        }
    }
    public void addLike(Post post, UpdatingPostCallback updatingPostCallback){
        postRepository.addLike(post,userRepository.getUser(),updatingPostCallback);
    }
    public void removeLike(Post post, UpdatingPostCallback updatingPostCallback){
        postRepository.removeLike(post,userRepository.getUser(),updatingPostCallback);
    }
    public void addComment(Post post, String commentString,UpdatingPostCallback updatingPostCallback){
        postRepository.addComment(post,userRepository.getUser(),commentString,updatingPostCallback);
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
    public void removePostFromFavorites(PostItem postItem){
        userRepository.removePostFromFavorites(postItem.getPost(), new UpdatingUserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                if(favoritesPostItemListLiveData.getValue() != null){
                    Log.d("MY LOGS", "removing from favorites");
                    ArrayList<PostItem> postItems = favoritesPostItemListLiveData.getValue();
                    postItems.remove(postItem);
                    favoritesPostItemListLiveData.setValue(postItems);
                }
            }

            @Override
            public void onFailure(String textError) {

            }
        });
    }
    public void selectAlbum(Album album){
        selectedAlbumRepository.selectAlbum(album);
    }
    public void loadData(Context context){
        if(userRepository.getUser().getFavoritesAlbumList() != null){
            albumListLiveData.setValue(userRepository.getUser().getFavoritesAlbumList());
        }
        if(userRepository.getUser().getFavoritesPostList() != null){
            LoadingPopup.show(context);
            ArrayList<PostItem> postItemsList = new ArrayList<>();
            ArrayList<String> favoritesPostList = userRepository.getUser().getFavoritesPostList();
            for(String postId : favoritesPostList){
                postRepository.getPostById(postId, new GettingPostByIdCallback() {
                    @Override
                    public void onSuccess(Post post) {
                        userRepository.getUserByID(post.getAuthorId(), new GettingUserByIdCallback() {
                            @Override
                            public void onSuccess(User user) {
                                postItemsList.add(new PostItem(post,user.getUsername(),user.getProfilename(),
                                        user.getProfilePhotoDownloadUri(),checkOnLike(post),true));
                                Log.d("MY LOGS","parse post >>: " + post.getPostId());
                                favoritesPostItemListLiveData.setValue(sortPostItemListById(favoritesPostList,postItemsList));
                                LoadingPopup.hide(context);
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        }
    }
    private ArrayList<PostItem> sortPostItemListById(ArrayList<String> postIdList, ArrayList<PostItem> postItemList){
        PostItem[] sortedPostItemList = new PostItem[postItemList.size()];
        ArrayList<PostItem> sortedPostItemArrayList = new ArrayList<>();
        for(PostItem postItem : postItemList) {
            if (postItem.getPost() != null) {
                if (postIdList.contains(postItem.getPost().getPostId())) {
                    if (postIdList.indexOf(postItem.getPost().getPostId()) < sortedPostItemList.length) {
                        sortedPostItemList[postIdList.indexOf(postItem.getPost().getPostId())] = postItem;
                    } else {
                        sortedPostItemList[sortedPostItemList.length - 1] = postItem;
                    }
                }
            }
        }
        Collections.addAll(sortedPostItemArrayList,sortedPostItemList);
        return sortedPostItemArrayList;
    }
}