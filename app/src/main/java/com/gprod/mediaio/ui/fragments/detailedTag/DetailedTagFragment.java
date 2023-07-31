package com.gprod.mediaio.ui.fragments.detailedTag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.PostListAdapter;
import com.gprod.mediaio.interfaces.adapters.AddCommentClickListener;
import com.gprod.mediaio.interfaces.adapters.AddToFavoritesClickListener;
import com.gprod.mediaio.interfaces.adapters.LikeClickListener;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.ui.dialogs.comments.CommentsDialog;

import java.util.ArrayList;

public class DetailedTagFragment extends Fragment {

    private DetailedTagViewModel viewModel;
    private RecyclerView postListView;
    private PostListAdapter postListAdapter;
    private LiveData<ArrayList<PostItem>> postItemListLiveData;
    private CommentsDialog commentsDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DetailedTagViewModel.class);
        View view = inflater.inflate(R.layout.detailed_tag_fragment_, container, false);
        postListView = view.findViewById(R.id.detailedTagPostListView);
        commentsDialog = CommentsDialog.getInstance(getActivity(),getActivity());
        postItemListLiveData = viewModel.getPostItemListLiveData();
        postListAdapter = new PostListAdapter(getContext(),postItemListLiveData.getValue(),commentsDialog);
        postListView.setLayoutManager(new LinearLayoutManager(getContext()));
        postListView.setAdapter(postListAdapter);
        ((SimpleItemAnimator) postListView.getItemAnimator()).setSupportsChangeAnimations(false);
        LikeClickListener likeClickListener = new LikeClickListener() {
            @Override
            public void onClick(View itemView, PostItem postItem) {
                if(postItem.isOnLike()){
                    viewModel.removeLike(postItem.getPost(), new UpdatingPostCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            postItem.setOnLike(false);
                            postItem.setPost(post);
                            postListAdapter.updateItem(getContext(),postItem);
                        }
                        @Override
                        public void onFailure() {
                            //TODO:Notify
                        }
                    });
                }
                else {
                    viewModel.addLike(postItem.getPost(), new UpdatingPostCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            postItem.setOnLike(true);
                            postItem.setPost(post);
                            postListAdapter.updateItem(getContext(),postItem);
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }
            }
        };
        AddCommentClickListener addCommentClickListener = new AddCommentClickListener() {
            @Override
            public void onCLick(PostItem postItem, String text) {
                viewModel.addComment(postItem.getPost(), text, new UpdatingPostCallback() {
                    @Override
                    public void onSuccess(Post post) {
                        postItem.setPost(post);
                        postListAdapter.updateItem(getContext(),postItem);
                    }

                    @Override
                    public void onFailure() {
                        //TODO:Notify user
                    }
                });
            }
        };
        AddToFavoritesClickListener addToFavoritesClickListener = new AddToFavoritesClickListener() {
            @Override
            public void onCLick(PostItem postItem) {
                if(postItem.isOnFavorites()){
                    viewModel.removePostFromFavorites(postItem.getPost(), new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            postItem.setOnFavorites(false);
                            postListAdapter.updateItem(getContext(),postItem);
                        }

                        @Override
                        public void onFailure(String textError) {
                            //TODO:Notify
                        }
                    });
                }
                else {
                    viewModel.addPostToFavorites(postItem.getPost(), new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            postItem.setOnFavorites(true);
                            postListAdapter.updateItem(getContext(),postItem);
                        }

                        @Override
                        public void onFailure(String textError) {
                            //TODO:Notify
                        }
                    });
                }
            }
        };
        postListAdapter.setAddCommentClickListener(addCommentClickListener);
        postListAdapter.setLikeClickListener(likeClickListener);
        postListAdapter.setAddToFavoritesClickListener(addToFavoritesClickListener);
        postItemListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<PostItem>>() {
            @Override
            public void onChanged(ArrayList<PostItem> postItems) {
                postListAdapter.updatePostItemList(postItems);
            }
        });
        viewModel.loadPostList();
        return view;
    }
}