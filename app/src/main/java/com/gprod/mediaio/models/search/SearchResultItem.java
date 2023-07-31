package com.gprod.mediaio.models.search;

import android.view.View;

import com.gprod.mediaio.enums.search.SearchResultItemsTypes;
import com.gprod.mediaio.interfaces.adapters.SearchResultItemClickListener;

public abstract class SearchResultItem {
    private SearchResultItemsTypes searchResultItemType;
    public SearchResultItem(SearchResultItemsTypes searchResultItemType){
        this.searchResultItemType = searchResultItemType;
    }
    public SearchResultItemsTypes getSearchResultItemType(){
        return searchResultItemType;
    }
}
