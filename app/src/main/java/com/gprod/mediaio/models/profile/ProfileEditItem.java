package com.gprod.mediaio.models.profile;

import android.view.View;

import com.gprod.mediaio.enums.profile.ProfileItemTypes;

public class ProfileEditItem extends ProfileItem{
    private View.OnClickListener clickListener;
    public ProfileEditItem(View.OnClickListener clickListener) {
        super(ProfileItemTypes.PROFILE_EDIT_ITEM);
        this.clickListener = clickListener;
    }
    public View.OnClickListener getClickListener() {
        return clickListener;
    }
}
