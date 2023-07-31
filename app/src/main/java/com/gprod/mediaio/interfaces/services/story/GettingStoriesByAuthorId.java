package com.gprod.mediaio.interfaces.services.story;

import java.util.ArrayList;

public interface GettingStoriesByAuthorId {
    void onSuccess(ArrayList<String> storiesId);
    void onFailure();
}
