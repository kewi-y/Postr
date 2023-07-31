package com.gprod.mediaio.repositories;

import com.gprod.mediaio.models.album.Album;

public class SelectedAlbumRepository {
    private Album selectedAlbum;
    private static SelectedAlbumRepository instance;

    public static SelectedAlbumRepository getInstance() {
        if(instance == null){
            instance = new SelectedAlbumRepository();
        }
        return instance;
    }
    private SelectedAlbumRepository(){}
    public void selectAlbum(Album album){
        selectedAlbum = album;
    }
    public void cleatSelectedAlbum(){
        selectedAlbum = null;
    }
    public Album getSelectedAlbum(){
        return selectedAlbum;
    }
}
