package com.gprod.mediaio.models.profile;

import com.gprod.mediaio.enums.profile.ProfileItemTypes;
import com.gprod.mediaio.models.user.User;

public class ProfileInfoItem extends ProfileItem {
    private User user;
    public ProfileInfoItem(User user) {
        super(ProfileItemTypes.PROFILE_INFO_ITEM);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
