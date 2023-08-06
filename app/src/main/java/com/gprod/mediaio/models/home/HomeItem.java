package com.gprod.mediaio.models.home;

import com.gprod.mediaio.enums.home.HomeItemTypes;

public abstract class HomeItem {
    private HomeItemTypes homeItemTypes;
    public HomeItem(HomeItemTypes homeItemTypes){
        this.homeItemTypes = homeItemTypes;
    }

    public HomeItemTypes getHomeItemTypes() {
        return homeItemTypes;
    }
}
