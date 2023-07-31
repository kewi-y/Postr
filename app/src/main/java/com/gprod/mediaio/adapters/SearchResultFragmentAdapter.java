package com.gprod.mediaio.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gprod.mediaio.enums.search.SearchResultItemsTypes;
import com.gprod.mediaio.models.search.SearchResultItem;
import com.gprod.mediaio.ui.fragments.search.searchResult.hashtags.SearchResultHashtagsFragment;
import com.gprod.mediaio.ui.fragments.search.searchResult.users.SearchResultUsersFragment;

import java.util.ArrayList;

public class SearchResultFragmentAdapter extends FragmentStateAdapter {
    private ArrayList<SearchResultItem> searchResultProfileItemList = new ArrayList<>();
    private ArrayList<SearchResultItem> searchResultTagItemList = new ArrayList<>();
    public SearchResultFragmentAdapter(@NonNull Fragment fragment, ArrayList<SearchResultItem> searchResultItemList) {
        super(fragment);
        for(SearchResultItem searchResultItem : searchResultItemList){
            if(searchResultItem.getSearchResultItemType().equals(SearchResultItemsTypes.PROFILE_ITEM)){
                searchResultProfileItemList.add(searchResultItem);
            }
            if(searchResultItem.getSearchResultItemType().equals(SearchResultItemsTypes.TAG_ITEM)){
                searchResultTagItemList.add(searchResultItem);
            }
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new SearchResultUsersFragment(searchResultProfileItemList);
        }
        else {
            return new SearchResultHashtagsFragment(searchResultTagItemList);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
    public void updateSearchResultItemList(ArrayList<SearchResultItem> searchResultItemList){
        for(SearchResultItem searchResultItem : searchResultItemList){
            if(searchResultItem.getSearchResultItemType().equals(SearchResultItemsTypes.PROFILE_ITEM)){
                searchResultProfileItemList.add(searchResultItem);
            }
            if(searchResultItem.getSearchResultItemType().equals(SearchResultItemsTypes.TAG_ITEM)){
                searchResultTagItemList.add(searchResultItem);
            }
        }
    }
}
