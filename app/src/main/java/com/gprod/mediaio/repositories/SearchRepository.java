package com.gprod.mediaio.repositories;

import com.gprod.mediaio.interfaces.services.database.SearchingTagCallback;
import com.gprod.mediaio.interfaces.services.database.SearchingUserCallback;
import com.gprod.mediaio.services.database.firebase.FirebaseDatabaseService;

public class SearchRepository {
    private FirebaseDatabaseService firebaseDatabaseService;
    private static SearchRepository instance;

    public static SearchRepository getInstance() {
        if(instance == null){
            instance = new SearchRepository();
        }
        return instance;
    }
    private SearchRepository(){
        firebaseDatabaseService = FirebaseDatabaseService.getInstance();
    }

    public void searchTag(String searchQuery, SearchingTagCallback searchingTagCallback){
        firebaseDatabaseService.searchTag(searchQuery,searchingTagCallback);
    }
    public void searchUser(String searchQuery, SearchingUserCallback searchingUserCallback){
        firebaseDatabaseService.searchUser(searchQuery,searchingUserCallback);
    }
}
