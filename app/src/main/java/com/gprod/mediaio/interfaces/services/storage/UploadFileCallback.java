package com.gprod.mediaio.interfaces.services.storage;

import android.net.Uri;

public interface UploadFileCallback {
    public void onSuccess(Uri downloadUri);
    public void onFailure();
}
