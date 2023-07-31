package com.gprod.mediaio.interfaces.services.database;

import com.gprod.mediaio.models.user.User;

import java.util.ArrayList;

public interface SearchingUserCallback {
    void onResult(ArrayList<User> userList);
}
