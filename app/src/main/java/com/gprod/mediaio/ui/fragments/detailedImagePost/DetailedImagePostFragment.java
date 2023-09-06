package com.gprod.mediaio.ui.fragments.detailedImagePost;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.PostListAdapter;
import com.gprod.mediaio.interfaces.adapters.AddCommentClickListener;
import com.gprod.mediaio.interfaces.adapters.AddToFavoritesClickListener;
import com.gprod.mediaio.interfaces.adapters.LikeClickListener;
import com.gprod.mediaio.interfaces.dialogs.comments.AddCommentListener;
import com.gprod.mediaio.interfaces.fragments.detailedImagePost.TagClickListener;
import com.gprod.mediaio.interfaces.services.database.GettingTagByStringTagCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.Tag;
import com.gprod.mediaio.ui.dialogs.comments.CommentsDialog;

import java.util.ArrayList;

public class DetailedImagePostFragment extends Fragment {

    private DetailedImagePostViewModel viewModel;
    private NavHostFragment navHostFragment;
    private RecyclerView postView;
    private CommentsDialog commentsDialog;
    private LiveData<ArrayList<PostItem>> postItemListLiveData;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detailed_image_post_fragment, container, false);
        viewModel = new ViewModelProvider(this).get(DetailedImagePostViewModel.class);
        commentsDialog = CommentsDialog.getInstance(getContext(),getActivity());
        postView = root.findViewById(R.id.postView);
        postItemListLiveData = viewModel.getPostItemListLiveData();
        postView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        PostListAdapter postListAdapter = new PostListAdapter(getContext(),postItemListLiveData.getValue(),commentsDialog);
        postView.setAdapter(postListAdapter);
        LikeClickListener likeClickListener = new LikeClickListener() {
            @Override
            public void onClick(View itemView, PostItem postItem) {
                if(postItem.isOnLike()){
                    viewModel.removeLike();
                }
                else{
                    viewModel.addLike();
                }
            }
        };
        AddCommentClickListener addCommentClickListener = new AddCommentClickListener() {
            @Override
            public void onCLick(PostItem postItem, String text) {
                viewModel.addComment(text);
            }
        };
        AddToFavoritesClickListener addToFavoritesClickListener = new AddToFavoritesClickListener() {
            @Override
            public void onCLick(PostItem postItem) {
                if(postItem.isOnFavorites()){
                    viewModel.removePostFromFavorites();
                }
                else {
                    viewModel.addPostToFavorites();
                }
            }
        };
        postListAdapter.setAddToFavoritesClickListener(addToFavoritesClickListener);
        postListAdapter.setAddCommentClickListener(addCommentClickListener);
        postListAdapter.setLikeClickListener(likeClickListener);
        postItemListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<PostItem>>() {
            @Override
            public void onChanged(ArrayList<PostItem> postItems) {
                postListAdapter.updatePostItemList(postItems);
            }
        });
        viewModel.loadData();
        return root;
    }
}