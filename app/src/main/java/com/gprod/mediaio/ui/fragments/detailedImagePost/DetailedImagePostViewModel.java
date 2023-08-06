package com.gprod.mediaio.ui.fragments.detailedImagePost;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostByIdCallback;
import com.gprod.mediaio.interfaces.services.database.GettingTagByStringTagCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Tag;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.SelectedPostRepository;
import com.gprod.mediaio.repositories.SelectedTagRepository;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;

public class DetailedImagePostViewModel extends ViewModel {
    private MutableLiveData<User> authorLiveData= new MutableLiveData<>();
    private MutableLiveData<ImagePost> postLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> userLikeLiveData = new MutableLiveData<>();
    private SelectedPostRepository selectedPostRepository;
    private SelectedTagRepository selectedTagRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    public DetailedImagePostViewModel(){
        userRepository = UserRepository.getInstance();
        postRepository = PostRepository.getInstance();
        selectedPostRepository = SelectedPostRepository.getInstance();
        selectedTagRepository = SelectedTagRepository.getInstance();
    }
    public LiveData<User> getAuthorLiveData(){
        return authorLiveData;
    }
    public LiveData<ImagePost> getPostLiveData(){
        return postLiveData;
    }
    public LiveData<Boolean> getUserLikeLiveData(){ return userLikeLiveData; }


    public void loadData(){
        getPostData(new GettingPostByIdCallback() {
            @Override
            public void onSuccess(Post post) {
                checkOnLike();
                getPostAuthorData(post);
            }

            @Override
            public void onFailure() {
                //TODO: Notify user
            }
        });
    }
    private void getPostData(GettingPostByIdCallback gettingPostByIdCallback){
        postRepository.getPostById(selectedPostRepository.getSelectedPost().getPostId(), new GettingPostByIdCallback() {
            @Override
            public void onSuccess(Post post) {
                if(post.getPostType().equals(PostTypes.IMAGE_POST)){
                    postLiveData.setValue((ImagePost) post);
                    gettingPostByIdCallback.onSuccess(post);
                }
            }
            @Override
            public void onFailure() {
                gettingPostByIdCallback.onFailure();
            }
        });
    }
    private void getPostAuthorData(Post post){
        userRepository.getUserByID(post.getAuthorId(), new GettingUserByIdCallback() {
            @Override
            public void onSuccess(User user) {
                authorLiveData.setValue(user);
            }

            @Override
            public void onFailure() {
                //TODO: Notify User
            }
        });

    }

   public void checkOnLike(){
        if(selectedPostRepository.getSelectedPost().getLikes() != null){
            if(selectedPostRepository.getSelectedPost().getLikes().contains(userRepository.getUser().getId())){
                userLikeLiveData.setValue(true);
            }
            else{
                userLikeLiveData.setValue(false);
            }
        }
        else {
            userLikeLiveData.setValue(false);
        }
    }
    public boolean checkOnFavorites(){
        if(userRepository.getUser().getFavoritesPostList() != null){
            return userRepository.getUser().getFavoritesPostList().contains(selectedPostRepository.getSelectedPost().getPostId());
        }
        else {
            return false;
        }
    }
    public void addLike(UpdatingPostCallback updatingPostCallback){
        Post updatablePost = selectedPostRepository.getSelectedPost();
        postRepository.addLike(updatablePost, userRepository.getUser(), new UpdatingPostCallback() {
            @Override
            public void onSuccess(Post post) {
                postLiveData.setValue((ImagePost) post);
                updatingPostCallback.onSuccess(post);
            }

            @Override
            public void onFailure() {
                updatingPostCallback.onFailure();
            }
        });
    }

    public void removeLike(UpdatingPostCallback updatingPostCallback){
        Post updatablePost = selectedPostRepository.getSelectedPost();
        postRepository.removeLike(updatablePost, userRepository.getUser(), new UpdatingPostCallback() {
            @Override
            public void onSuccess(Post post) {
                postLiveData.setValue((ImagePost) post);
                updatingPostCallback.onSuccess(post);
            }

            @Override
            public void onFailure() {
                updatingPostCallback.onFailure();
            }
        });
    }

    public void addComment(String commentContent,UpdatingPostCallback updatingPostCallback){
        Post updatablePost = selectedPostRepository.getSelectedPost();
        postRepository.addComment(updatablePost, userRepository.getUser(), commentContent, new UpdatingPostCallback() {
            @Override
            public void onSuccess(Post post) {
                postLiveData.setValue((ImagePost) post);
                updatingPostCallback.onSuccess(post);
            }

            @Override
            public void onFailure() {
                updatingPostCallback.onFailure();
            }
        });
    }
    public void addPostToFavorites(UpdatingUserCallback updatingUserCallback){
        userRepository.addPostToFavorites(selectedPostRepository.getSelectedPost(),updatingUserCallback);
    }
    public void removePostFromFavorites(UpdatingUserCallback updatingUserCallback){
        userRepository.removePostFromFavorites(selectedPostRepository.getSelectedPost(),updatingUserCallback);
    }
    public void getTagByStringTag(String tag, GettingTagByStringTagCallback gettingTagByStringTagCallback){
        postRepository.getTagByStringTag(tag,gettingTagByStringTagCallback);
    }
    public void selectTag(Tag tag){
        selectedTagRepository.setSelectedTag(tag);
        postRepository.addViewCountForTag(tag);
    }
}