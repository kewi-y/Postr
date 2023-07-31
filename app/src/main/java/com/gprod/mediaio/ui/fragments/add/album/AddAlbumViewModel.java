package com.gprod.mediaio.ui.fragments.add.album;



import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.repositories.user.AddAlbumCallback;
import com.gprod.mediaio.interfaces.repositories.user.UpdatingAlbumCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostByIdCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.album.Album;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.UserRepository;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;

import java.util.ArrayList;

public class AddAlbumViewModel extends ViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private ArrayList<Post> favoritesPostList = new ArrayList<>();
    private MutableLiveData<ArrayList<Post>> favoritesPostListLiveData = new MutableLiveData<>();
    public AddAlbumViewModel() {
        userRepository = UserRepository.getInstance();
        postRepository = PostRepository.getInstance();
    }

    public LiveData<ArrayList<Post>> getFavoritesPostListLiveData() {
        return favoritesPostListLiveData;
    }
    public void addAlbum(ArrayList<Post> posts, String albumName, AddAlbumCallback addAlbumCallback){
        userRepository.addAlbum(posts.get(0), albumName, new AddAlbumCallback() {
            @Override
            public void onSuccess(String albumId) {
                if(posts.size() > 1) {
                    for (int i = 1; i < posts.size(); i++) {
                        userRepository.addPostToAlbum(posts.get(i), albumId,new UpdatingAlbumCallback() {
                            @Override
                            public void onSuccess(Album updatedAlbum) {
                                addAlbumCallback.onSuccess(updatedAlbum.getId());
                            }

                            @Override
                            public void onFailure() {
                                addAlbumCallback.onFailure();
                            }
                        });
                    }
                }
                else {
                    addAlbumCallback.onSuccess(albumId);
                }
            }

            @Override
            public void onFailure() {
                addAlbumCallback.onFailure();
            }
        });
    }
    public void loadData(Context context){
        if(userRepository.getUser().getFavoritesPostList() != null){
            LoadingPopup.show(context);
            for(String postId : userRepository.getUser().getFavoritesPostList()){
                postRepository.getPostById(postId, new GettingPostByIdCallback() {
                    @Override
                    public void onSuccess(Post post) {
                        favoritesPostList.add(post);
                        favoritesPostListLiveData.setValue(favoritesPostList);
                        LoadingPopup.hide(context);
                    }
                    @Override
                    public void onFailure() {
                        LoadingPopup.hide(context);
                    }
                });
            }
        }
    }
}