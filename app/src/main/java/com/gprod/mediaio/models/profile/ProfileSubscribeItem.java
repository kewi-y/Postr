package com.gprod.mediaio.models.profile;

import android.view.View;

import com.gprod.mediaio.enums.profile.ProfileItemTypes;

public class ProfileSubscribeItem extends ProfileItem{
    private View.OnClickListener onClickListener;
    public ProfileSubscribeItem(View.OnClickListener clickListener) {
        super(ProfileItemTypes.PROFILE_SUBSCRIBE_ITEM);
        this.onClickListener = clickListener;
    }
    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
