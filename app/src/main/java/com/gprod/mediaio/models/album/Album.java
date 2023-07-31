package com.gprod.mediaio.models.album;

import java.util.ArrayList;

public class Album {
    private String id, name;
    private ArrayList<String> postIdList;
    private String previewImageDownloadUri;
    public Album(String id,String name, ArrayList<String> postIdList, String previewImageDownloadUri){
        this.id = id;
        this.name = name;
        this.postIdList = postIdList;
        this.previewImageDownloadUri = previewImageDownloadUri;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getPostIdList() {
        return postIdList;
    }

    public String getPreviewImageDownloadUri() {
        return previewImageDownloadUri;
    }

    public String getName() {
        return name;
    }
}
