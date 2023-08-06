package com.gprod.mediaio.interfaces.fragments.add.story;

public interface RecordingVideoStoryCallback {
    void onStarted();
    void onProgress(int percent);
    void onFinish();
    void onUploadFileProgress(int percent);
    void onFailure();
}
