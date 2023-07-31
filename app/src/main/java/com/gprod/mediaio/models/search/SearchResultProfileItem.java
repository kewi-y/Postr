package com.gprod.mediaio.models.search;

import com.gprod.mediaio.enums.search.SearchResultItemsTypes;
import com.gprod.mediaio.models.user.User;

public class SearchResultProfileItem extends SearchResultItem{
    private User user;
    public SearchResultProfileItem(User user) {
        super(SearchResultItemsTypes.PROFILE_ITEM);
        this.user = user;
    }
    public User getUser() {return user;}
}
