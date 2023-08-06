package com.gprod.mediaio.models.home;

import android.content.Context;
import android.view.ContextMenu;

import com.gprod.mediaio.adapters.PostListAdapter;
import com.gprod.mediaio.enums.home.HomeItemTypes;
import com.gprod.mediaio.interfaces.adapters.AddCommentClickListener;
import com.gprod.mediaio.interfaces.adapters.AddToFavoritesClickListener;
import com.gprod.mediaio.interfaces.adapters.LikeClickListener;
import com.gprod.mediaio.interfaces.adapters.PostAuthorClickListener;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.ui.dialogs.comments.CommentsDialog;

import java.util.ArrayList;

public class FeedHomeItem extends HomeItem {
    private ArrayList<PostItem> postItemList;
    private CommentsDialog commentsDialog;
    private PostListAdapter postListAdapter;
    private LikeClickListener likeClickListener;
    private AddCommentClickListener addCommentClickListener;
    private AddToFavoritesClickListener addToFavoritesClickListener;
    private PostAuthorClickListener postAuthorClickListener;


    public FeedHomeItem(Context context, ArrayList<PostItem> postItemList, CommentsDialog commentsDialog) {
        super(HomeItemTypes.FEED_ITEM);
        this.postItemList = postItemList;
        this.commentsDialog = commentsDialog;
        postListAdapter = new PostListAdapter(context,postItemList,commentsDialog);
        postListAdapter.setAddCommentClickListener(addCommentClickListener);
        postListAdapter.setLikeClickListener(likeClickListener);
        postListAdapter.setAddCommentClickListener(addCommentClickListener);
    }
    public void updatePostsItems(ArrayList<PostItem> postItemList){
        this.postItemList = postItemList;
        postListAdapter.updatePostItemList(postItemList);
    }
    public void updatePostItem(Context context,PostItem postItem){
        postListAdapter.updateItem(context,postItem);
    }

    public void setLikeClickListener(LikeClickListener likeClickListener) {
        this.likeClickListener = likeClickListener;
        postListAdapter.setLikeClickListener(likeClickListener);
    }

    public void setAddCommentClickListener(AddCommentClickListener addCommentClickListener) {
        this.addCommentClickListener = addCommentClickListener;
        postListAdapter.setAddCommentClickListener(addCommentClickListener);
    }
    public void setPostAuthorClickListener(PostAuthorClickListener postAuthorClickListener){
        this.postAuthorClickListener = postAuthorClickListener;
        postListAdapter.setPostAuthorClickListener(postAuthorClickListener);
    }

    public void setAddToFavoritesClickListener(AddToFavoritesClickListener addToFavoritesClickListener) {
        this.addToFavoritesClickListener = addToFavoritesClickListener;
        postListAdapter.setAddToFavoritesClickListener(addToFavoritesClickListener);
    }

    public PostListAdapter getPostListAdapter() {
        return postListAdapter;
    }
}
