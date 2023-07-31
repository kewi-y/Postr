package com.gprod.mediaio.interfaces.services.camera;

import android.graphics.Bitmap;

public interface TakingPhotoCallback {
    void onTaken(Bitmap image);
    void onFailure();
}
