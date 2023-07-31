package com.gprod.mediaio.models.profile;

import android.view.View;

import com.gprod.mediaio.enums.profile.ProfileItemTypes;

import java.util.ArrayList;

public abstract class ProfileItem {
    private ProfileItemTypes profileItemType;
    public ProfileItem(ProfileItemTypes profileItemType){
        this.profileItemType = profileItemType;
    }

    public ProfileItemTypes getProfileItemType() {
        return profileItemType;
    }
}
