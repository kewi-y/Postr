package com.gprod.mediaio.interfaces.services.storage;

import android.net.Uri;

public interface GettingFileDownloadUriCallback {
    void onSuccess(Uri uri);
    void onFailure();
}
