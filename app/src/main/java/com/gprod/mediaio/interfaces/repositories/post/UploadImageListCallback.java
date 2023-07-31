package com.gprod.mediaio.interfaces.repositories.post;

import java.util.ArrayList;

public interface UploadImageListCallback {
    void onSuccess(ArrayList<String> uploadedImageDownloadUriList);
    void onFailure();
}
