package com.gprod.mediaio.interfaces.repositories.user;

public interface AddAlbumCallback {
    void onSuccess(String albumId);
    void onFailure();
}
