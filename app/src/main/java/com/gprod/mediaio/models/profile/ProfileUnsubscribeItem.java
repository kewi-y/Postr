package com.gprod.mediaio.models.profile;

import android.view.View;

import com.gprod.mediaio.enums.profile.ProfileItemTypes;

public class ProfileUnsubscribeItem extends ProfileItem{
    private View.OnClickListener onClickListener;
    public ProfileUnsubscribeItem(View.OnClickListener unsubscribeClickListener) {
        super(ProfileItemTypes.PROFILE_UNSUBSCRIBE_ITEM);
        this.onClickListener = unsubscribeClickListener;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
