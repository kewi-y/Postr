package com.gprod.mediaio.interfaces.repositories.user;

import com.gprod.mediaio.models.album.Album;

public interface UpdatingAlbumCallback {
    void onSuccess(Album updatedAlbum);
    void onFailure();
}
