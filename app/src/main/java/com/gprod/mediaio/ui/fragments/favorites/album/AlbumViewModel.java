package com.gprod.mediaio.ui.fragments.favorites.album;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.adapters.AttachedImageListAdapter;
import com.gprod.mediaio.interfaces.repositories.user.UpdatingAlbumCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostByIdCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.album.Album;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.SelectedAlbumRepository;
import com.gprod.mediaio.repositories.SelectedPostRepository;
import com.gprod.mediaio.repositories.UserRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AlbumViewModel extends ViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private SelectedAlbumRepository selectedAlbumRepository;
    private SelectedPostRepository selectedPostRepository;
    private MutableLiveData<ArrayList<Post>> albumPostListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Post>> favoritesPostListLiveData = new MutableLiveData<>();
    private MutableLiveData<String> albumNameLiveData = new MutableLiveData<>();
    private ArrayList<Post> albumPostList = new ArrayList<>();
    private ArrayList<Post> favoritesPostList = new ArrayList<>();

    public AlbumViewModel(){
        userRepository = UserRepository.getInstance();
        postRepository = PostRepository.getInstance();
        selectedPostRepository = SelectedPostRepository.getInstance();
        selectedAlbumRepository = SelectedAlbumRepository.getInstance();
    }

    public LiveData<ArrayList<Post>> getAlbumPostListLiveData() {
        return albumPostListLiveData;
    }

    public LiveData<String> getAlbumNameLiveData() {
        return albumNameLiveData;
    }

    public LiveData<ArrayList<Post>> getFavoritesPostListLiveData() {
        return favoritesPostListLiveData;
    }

    public void selectPost(Post post){
        selectedPostRepository.setSelectedPost(post);
    }
    public void removePost(Post post){
        userRepository.removePostFromAlbum(post, selectedAlbumRepository.getSelectedAlbum().getId(), new UpdatingAlbumCallback() {
            @Override
            public void onSuccess(Album updatedAlbum) {
                selectedAlbumRepository.selectAlbum(updatedAlbum);
                loadData();
            }

            @Override
            public void onFailure() {

            }
        });
    }
    public void addPostList(ArrayList<Post> posts){
        for(Post post : posts){
            userRepository.addPostToAlbum(post, selectedAlbumRepository.getSelectedAlbum().getId(), new UpdatingAlbumCallback() {
                @Override
                public void onSuccess(Album updatedAlbum) {
                    selectedAlbumRepository.selectAlbum(updatedAlbum);
                    loadData();
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    public void loadData(){
        if(albumPostList.size() != 0){
            albumPostList.clear();
        }
        if(favoritesPostList.size() != 0){
            favoritesPostList.clear();
        }
        if(selectedAlbumRepository.getSelectedAlbum() != null &&
                userRepository.getUser().getFavoritesPostList() != null){
            albumNameLiveData.setValue(selectedAlbumRepository.getSelectedAlbum().getName());
            for(String postId : userRepository.getUser().getFavoritesPostList()){
                if(selectedAlbumRepository.getSelectedAlbum().getPostIdList().contains(postId)){
                    postRepository.getPostById(postId, new GettingPostByIdCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            albumPostList.add(post);
                            if(albumPostList.size() == selectedAlbumRepository.getSelectedAlbum().getPostIdList().size()) {
                                albumPostListLiveData.setValue(sortPostListById(selectedAlbumRepository.getSelectedAlbum().getPostIdList(),
                                        albumPostList));
                            }
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }
                else {
                    postRepository.getPostById(postId, new GettingPostByIdCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            favoritesPostList.add(post);
                            favoritesPostListLiveData.setValue(favoritesPostList);
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }
            }
        }
    }
    private ArrayList<Post> sortPostListById(ArrayList<String> postIdList, ArrayList<Post> postList){
        if(postIdList.size() == postList.size()){
            Post[] sortedPostList = new Post[postList.size()];
            ArrayList<Post> sortedPostArrayList = new ArrayList<>();
            for(Post post : postList){
                if(postIdList.contains(post.getPostId())){
                    sortedPostList[postIdList.indexOf(post.getPostId())] = post;
                }
            }
            Collections.addAll(sortedPostArrayList,sortedPostList);
            return sortedPostArrayList;
        }
        else {
            return null;
        }
    }
}