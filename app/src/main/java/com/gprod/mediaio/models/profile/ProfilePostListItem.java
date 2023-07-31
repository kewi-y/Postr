package com.gprod.mediaio.models.profile;

import android.content.Context;
import android.view.View;

import com.gprod.mediaio.adapters.PreviewPostListAdapter;
import com.gprod.mediaio.enums.profile.ProfileItemTypes;
import com.gprod.mediaio.interfaces.adapters.DeletePostListener;
import com.gprod.mediaio.interfaces.adapters.PostClickListener;
import com.gprod.mediaio.interfaces.adapters.DragPostListener;
import com.gprod.mediaio.models.post.Post;

import java.util.ArrayList;

public class ProfilePostListItem extends ProfileItem{
    private ArrayList<Post> posts;
    private PreviewPostListAdapter postListAdapter;
    private PostClickListener postCLickListener;
    private DeletePostListener deletePostListener;
    private DragPostListener dragPostListener;
    private View deletingPostFieldView;
    public ProfilePostListItem(Context context, ArrayList<Post> posts, View deletingPostFieldView , PostClickListener postCLickListener) {
        super(ProfileItemTypes.PROFILE_POST_LIST_ITEM);
        this.posts = posts;
        this.postCLickListener = postCLickListener;
        this.deletingPostFieldView = deletingPostFieldView;
        if(posts != null && posts.size() != 0){
            postListAdapter = new PreviewPostListAdapter(context,posts,deletingPostFieldView,postCLickListener);
        }
    }

    public PreviewPostListAdapter getPostListAdapter() {
        return postListAdapter;
    }

    public void updatePostList(Context context, ArrayList<Post> posts){
        if(postListAdapter != null){
            postListAdapter.setPostList(posts);
            postListAdapter.notifyDataSetChanged();
        }
        else{
            postListAdapter = new PreviewPostListAdapter(context,posts,deletingPostFieldView,postCLickListener);
        }
    }
    public void setDeletePostClickListener(DeletePostListener deletePostListener){
        this.deletePostListener = deletePostListener;
        if(postListAdapter != null) {
            postListAdapter.setDeletePostClickListener(deletePostListener);
        }
    }
    public void setStartDragPostListener(DragPostListener dragPostListener){
        this.dragPostListener = dragPostListener;
        if(postListAdapter != null){
            postListAdapter.setStartDragPostListener(dragPostListener);
        }
    }
}
