package com.gprod.mediaio.ui.dialogs.album;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.SelectablePostListAdapter;
import com.gprod.mediaio.interfaces.dialogs.album.AddPostsToAlbumCallback;
import com.gprod.mediaio.models.post.Post;

import java.util.ArrayList;

public class AddPostsToAlbumDialog {
    private static AddPostsToAlbumDialog instance;
    private RecyclerView addingPostListView;
    private MaterialButton addPostsButton;
    private SelectablePostListAdapter selectablePostListAdapter;
    private BottomSheetDialog dialog;
    private ArrayList<Post> favoritesPostList;
    public static AddPostsToAlbumDialog getInstance(Context context, Activity activity) {
        if(instance == null){
            instance = new AddPostsToAlbumDialog(context,activity);
        }
        return instance;
    }
    public AddPostsToAlbumDialog(Context context, Activity activity){
        dialog = new BottomSheetDialog(activity);
        dialog.setContentView(R.layout.add_posts_to_album_dialog);
        addingPostListView = dialog.findViewById(R.id.addingPostList);
        addPostsButton = dialog.findViewById(R.id.addPostsToAlbumButton);
        addingPostListView.setLayoutManager(new GridLayoutManager(context,3));
        selectablePostListAdapter = new SelectablePostListAdapter(context);
    }
    public void setPostList(ArrayList<Post> favoritesPostList){
        this.favoritesPostList = favoritesPostList;
    }
    public void show(AddPostsToAlbumCallback addPostsToAlbumCallback){
        if(favoritesPostList != null && favoritesPostList.size() != 0){
            selectablePostListAdapter.updatePostList(favoritesPostList);
            addingPostListView.setAdapter(selectablePostListAdapter);
        }
        addPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectablePostListAdapter.getSelectedPostList() != null &&
                        selectablePostListAdapter.getSelectedPostList().size() != 0){
                    addPostsToAlbumCallback.onAdd(selectablePostListAdapter.getSelectedPostList());
                    selectablePostListAdapter.clearSelectedPostList();
                    if(dialog.isShowing() == true){
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }
}
