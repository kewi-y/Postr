package com.gprod.mediaio.interfaces.fragments.profile;

import com.gprod.mediaio.enums.profile.ProfileTypes;

public interface LoadProfileCallback {
    void onLoaded(ProfileTypes profileType);
}
