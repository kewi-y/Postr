package com.gprod.mediaio.models.profile;

import android.view.View;

import com.gprod.mediaio.enums.profile.ProfileItemTypes;
import com.gprod.mediaio.interfaces.fragments.profile.ShareProfileListener;
import com.gprod.mediaio.models.user.User;


public class ProfileEditItem extends ProfileItem{
    private View.OnClickListener clickListener;
    private ShareProfileListener shareProfileListener;
    public ProfileEditItem(View.OnClickListener clickListener, ShareProfileListener shareProfileListener) {
        super(ProfileItemTypes.PROFILE_EDIT_ITEM);
        this.clickListener = clickListener;
        this.shareProfileListener = shareProfileListener;
    }
    public View.OnClickListener getClickListener() {
        return clickListener;
    }
    public ShareProfileListener getShareProfileListener(){
        return shareProfileListener;
    }
}
