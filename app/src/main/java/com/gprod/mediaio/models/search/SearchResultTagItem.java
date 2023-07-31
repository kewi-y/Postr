package com.gprod.mediaio.models.search;

import android.view.View;

import com.gprod.mediaio.enums.search.SearchResultItemsTypes;
import com.gprod.mediaio.interfaces.adapters.SearchResultItemClickListener;
import com.gprod.mediaio.models.post.Tag;

public class SearchResultTagItem extends SearchResultItem{
    private Tag tag;
    public SearchResultTagItem(Tag tag) {
        super(SearchResultItemsTypes.TAG_ITEM);
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }
}
