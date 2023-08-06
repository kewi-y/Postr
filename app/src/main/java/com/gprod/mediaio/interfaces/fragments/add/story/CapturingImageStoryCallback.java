package com.gprod.mediaio.interfaces.fragments.add.story;

import android.graphics.Bitmap;

public interface CapturingImageStoryCallback {
    void onCaptured();
    void onUploadFileProgress(int percent);
    void onFailure();
}
