package com.gprod.mediaio.ui.fragments.search;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.services.database.SearchingTagCallback;
import com.gprod.mediaio.interfaces.services.database.SearchingUserCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Tag;
import com.gprod.mediaio.models.search.SearchResultItem;
import com.gprod.mediaio.models.search.SearchResultProfileItem;
import com.gprod.mediaio.models.search.SearchResultTagItem;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.SearchRepository;
import com.gprod.mediaio.repositories.SelectedTagRepository;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel {
    private SearchRepository searchRepository;
    private PostRepository postRepository;
    private SelectedTagRepository selectedTagRepository;
    private SelectedUserRepository selectedUserRepository;
    private MutableLiveData<ArrayList<SearchResultItem>> searchResultItemListLiveData = new MutableLiveData<>();
    private ArrayList<SearchResultItem> searchResultItemList = new ArrayList<>();
    public SearchViewModel(){
        searchRepository = SearchRepository.getInstance();
        selectedUserRepository = SelectedUserRepository.getInstance();
        selectedTagRepository = SelectedTagRepository.getInstance();
        postRepository = PostRepository.getInstance();
    }

    public MutableLiveData<ArrayList<SearchResultItem>> getSearchResultItemListLiveData() {
        return searchResultItemListLiveData;
    }

    public void search(Context context, String searchQuery){
        //TODO: Remove context
        char hashtagChar = "#".charAt(0);
        char sightAtChar = "@".charAt(0);
        if(searchResultItemList.size() > 0){
            searchResultItemList.clear();
        }
        if(searchQuery.charAt(0) == hashtagChar){
            searchTag(searchQuery);
        }
        else if(searchQuery.charAt(0) == sightAtChar){
            searchUser(searchQuery);
        }
        else {
            searchAll(searchQuery);
        }
    }
    private void searchTag (String searchQuery){
        searchRepository.searchTag(searchQuery, new SearchingTagCallback() {
            @Override
            public void onResult(ArrayList<Tag> tagList) {
                for(Tag tag : tagList){
                    SearchResultTagItem searchResultTagItem = new SearchResultTagItem(tag);
                    searchResultItemList.add(searchResultTagItem);
                }
                searchResultItemListLiveData.setValue(searchResultItemList);
            }
        });
    }
    private void searchUser (String searchQuery){
        searchRepository.searchUser(searchQuery, new SearchingUserCallback() {
            @Override
            public void onResult(ArrayList<User> userList) {
                for(User user : userList){
                    SearchResultProfileItem searchResultProfileItem = new SearchResultProfileItem(user);
                    searchResultItemList.add(new SearchResultProfileItem(user));
                }
                searchResultItemListLiveData.setValue(searchResultItemList);
            }
        });
    }
    private void searchAll(String searchQuery){
        searchRepository.searchUser("@" + searchQuery, new SearchingUserCallback() {
            @Override
            public void onResult(ArrayList<User> userList) {
                for(User user : userList){
                    SearchResultProfileItem searchResultProfileItem = new SearchResultProfileItem(user);
                    searchResultItemList.add(searchResultProfileItem);
                }
                searchRepository.searchTag("#" + searchQuery, new SearchingTagCallback() {
                    @Override
                    public void onResult(ArrayList<Tag> tagList) {
                        for(Tag tag : tagList){
                            SearchResultTagItem searchResultTagItem = new SearchResultTagItem(tag);
                            searchResultItemList.add(searchResultTagItem);
                        }
                        searchResultItemListLiveData.setValue(searchResultItemList);
                    }
                });
            }
        });
    }
    public void selectUser(User user){
        selectedUserRepository.setSelectedUser(user);
    }
    public void selectTag(Tag tag){
        selectedTagRepository.setSelectedTag(tag);
        postRepository.addViewCountForTag(tag);
    }
}