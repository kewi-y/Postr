package com.gprod.mediaio.interfaces.services.database;

import com.gprod.mediaio.models.user.User;

public interface GettingUserByIdCallback {
    void onSuccess(User user);
    void onFailure();
}
