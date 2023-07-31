package com.gprod.mediaio.interfaces.adapters;

import android.view.View;

import com.gprod.mediaio.models.post.PostItem;

public interface LikeClickListener {
    void onClick(View itemView, PostItem postItem);
}
