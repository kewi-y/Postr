package com.gprod.mediaio.interfaces.services.camera;

public interface QrScanningCallback {
    void onScanned(String content);
    void onFailure();
}
