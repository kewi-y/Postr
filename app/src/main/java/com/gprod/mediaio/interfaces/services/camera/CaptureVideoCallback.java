package com.gprod.mediaio.interfaces.services.camera;

public interface CaptureVideoCallback {
    void onCaptured(byte[] file);
    void onFailure();
}
