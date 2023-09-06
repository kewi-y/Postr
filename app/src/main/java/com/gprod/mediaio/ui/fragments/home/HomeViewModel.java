package com.gprod.mediaio.ui.fragments.home;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.repositories.story.GettingStoryListByAuthorIdCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostListCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.models.story.Story;
import com.gprod.mediaio.models.story.StoryItem;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.NfcParsedUserRepository;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.StoryRepository;
import com.gprod.mediaio.repositories.UserRepository;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private UserRepository userRepository;
    private StoryRepository storyRepository;
    private PostRepository postRepository;
    private NfcParsedUserRepository nfcParsedUserRepository;
    private SelectedUserRepository selectedUserRepository;
    private ArrayList<StoryItem> storyItemList = new ArrayList<>();
    private ArrayList<PostItem> postItemList = new ArrayList<>();
    private String nfcParsedUserId = null;

    private MutableLiveData<ArrayList<StoryItem>>storyItemListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<PostItem>> postItemListLiveData = new MutableLiveData<>();
    private MutableLiveData<String> nfcParsedUserIdLiveData = new MutableLiveData<>();
    public HomeViewModel(){
        postRepository = PostRepository.getInstance();
        userRepository = UserRepository.getInstance();
        selectedUserRepository = SelectedUserRepository.getInstance();
        nfcParsedUserRepository = NfcParsedUserRepository.getInstance();
    }
    public void init(String apiUri){
        storyRepository = StoryRepository.getInstance(apiUri);
    }

    public LiveData<ArrayList<StoryItem>> getStoryItemListLiveData() {
        return storyItemListLiveData;
    }

    public LiveData<ArrayList<PostItem>> getPostItemListLiveData() {
        return postItemListLiveData;
    }
    public LiveData<String> getNfcParsedUserIdLiveData(){return nfcParsedUserIdLiveData;}

    public void loadStories(){
        storyRepository.getStoryListByAuthorId(userRepository.getUser().getId(), new GettingStoryListByAuthorIdCallback() {
            @Override
            public void onSuccess(ArrayList<Story> storyList) {
                if(storyList != null && storyList.size() > 0) {
                    storyItemList.clear();
                    StoryItem storyItem = new StoryItem(storyList, userRepository.getUser().getUsername(),
                            userRepository.getUser().getProfilename(), userRepository.getUser().getProfilePhotoDownloadUri());
                    storyItemList.add(0,storyItem);
                    storyItemListLiveData.setValue(storyItemList);
                }
            }

            @Override
            public void onFailure() {

                Log.d("MY LOGS","Failed to get story list");
            }
        });
        ArrayList<String> storiesAuthorIds = new ArrayList<>();
        storiesAuthorIds.addAll(userRepository.getUser().getSubscriptions());
        if(storyRepository != null) {
            for (String storyAuthorId : storiesAuthorIds) {
                storyRepository.getStoryListByAuthorId(storyAuthorId, new GettingStoryListByAuthorIdCallback() {
                    @Override
                    public void onSuccess(ArrayList<Story> storyList) {
                        Log.d("MY LOGS","Loading story by user id >>: " + storyAuthorId);
                        if(storyList != null && storyList.size() > 0) {
                            Log.d("MY LOGS", "Story list size >>: " + storyList.size());
                            userRepository.getUserByID(storyAuthorId, new GettingUserByIdCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    StoryItem storyItem = new StoryItem(storyList, user.getUsername(), user.getProfilename(), user.getProfilePhotoDownloadUri());
                                    storyItemList.add(storyItem);
                                    storyItemListLiveData.setValue(storyItemList);
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d("MY LOGS","Failed to get story list");
                    }
                });
            }
        }
    }
    public void loadPosts(Context context,int loadIterations){
        if(postItemList.size() > 0){
            postItemList.clear();
        }
        int dayInUnix = Integer.parseInt(context.getResources().getString(R.string.day_in_unix));
        long loadTimestamp = System.currentTimeMillis() - (dayInUnix * loadIterations);
        postRepository.getFeedPostList(loadTimestamp, userRepository.getUser().getSubscriptions(), new GettingPostListCallback() {
            @Override
            public void onSuccess(ArrayList<Post> posts) {
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

                        }
                    });
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }
    public void loadNfcParsedUser(){
        nfcParsedUserIdLiveData.setValue(nfcParsedUserRepository.getUserId());
        nfcParsedUserRepository.clearUserId();
    }
    public void addLike(Post post, UpdatingPostCallback updatingPostCallback){
        postRepository.addLike(post,userRepository.getUser(),updatingPostCallback);
    }
    public void removeLike(Post post, UpdatingPostCallback updatingPostCallback){
        postRepository.removeLike(post,userRepository.getUser(),updatingPostCallback);
    }
    public void addComment(Post post,String commentText,UpdatingPostCallback updatingPostCallback){
        postRepository.addComment(post,userRepository.getUser(),commentText,updatingPostCallback);
    }
    public void addToFavorites(Post post, UpdatingUserCallback updatingUserCallback){
        userRepository.addPostToFavorites(post,updatingUserCallback);
    }
    public void removeFromFavorites(Post post, UpdatingUserCallback updatingUserCallback){
        userRepository.removePostFromFavorites(post,updatingUserCallback);
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
            return userRepository.getUser().getFavoritesPostList().contains(post.getPostId());
        }
        else {
            return false;
        }
    }
}