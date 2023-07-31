package com.gprod.mediaio.interfaces.services.database;

import com.gprod.mediaio.models.user.User;

public interface UpdatingUserCallback {
    void onSuccess(User updatedUser);
    void onFailure(String textError);
}
