package com.gprod.mediaio.ui.fragments.favorites.album;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.PreviewPostListAdapter;
import com.gprod.mediaio.interfaces.adapters.DeletePostListener;
import com.gprod.mediaio.interfaces.adapters.DragPostListener;
import com.gprod.mediaio.interfaces.adapters.PostClickListener;
import com.gprod.mediaio.interfaces.dialogs.album.AddPostsToAlbumCallback;
import com.gprod.mediaio.interfaces.services.database.DeletingPostCallback;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.ui.dialogs.album.AddPostsToAlbumDialog;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    private AlbumViewModel viewModel;
    private TextView albumNameView;
    private RecyclerView albumPostListView;
    private PreviewPostListAdapter previewPostListAdapter;
    private LiveData<ArrayList<Post>> albumPostListLiveData;
    private LiveData<String> albumNameLiveData;
    private View removingFieldView;
    private NavController navController;
    private LiveData<ArrayList<Post>> favoritesPostListLiveData;
    private AddPostsToAlbumDialog addPostsToAlbumDialog;
    private ImageButton addPostsToAlbumButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AlbumViewModel.class);
        View root = inflater.inflate(R.layout.album_fragment, container, false);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        albumNameView = root.findViewById(R.id.albumNameView);
        removingFieldView = root.findViewById(R.id.removingPostFromAlbumField);
        albumPostListView = root.findViewById(R.id.albumPostListView);
        addPostsToAlbumButton = root.findViewById(R.id.addPostsToAlbumButton);
        albumPostListView.setLayoutManager(new GridLayoutManager(getContext(),3));
        addPostsToAlbumDialog = AddPostsToAlbumDialog.getInstance(getContext(),getActivity());
        albumPostListLiveData = viewModel.getAlbumPostListLiveData();
        albumNameLiveData = viewModel.getAlbumNameLiveData();
        favoritesPostListLiveData = viewModel.getFavoritesPostListLiveData();
        Animator showRemovingFieldAnimation = AnimatorInflater.loadAnimator(getContext(),R.animator.show_down);
        Animator hideRemovingFieldAnimation = AnimatorInflater.loadAnimator(getContext(),R.animator.hide_up);
        DeletePostListener deletePostListener = new DeletePostListener() {
            @Override
            public void onDelete(Post post) {
                viewModel.removePost(post);
            }
        };

        DragPostListener dragPostListener = new DragPostListener() {
            @Override
            public void onDrag() {
                removingFieldView.bringToFront();
                showRemovingFieldAnimation.setTarget(removingFieldView);
                removingFieldView.setVisibility(View.VISIBLE);
                showRemovingFieldAnimation.start();
            }

            @Override
            public void onEnd() {
                hideRemovingFieldAnimation.setTarget(removingFieldView);
                hideRemovingFieldAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        removingFieldView.setVisibility(View.INVISIBLE);
                    }
                });
                hideRemovingFieldAnimation.start();
            }
        };

        PostClickListener postClickListener = new PostClickListener() {
            @Override
            public void onClick(Post post) {
                viewModel.selectPost(post);
                navController.navigate(R.id.detailedPostFragment);
            }
        };
        AddPostsToAlbumCallback addPostsToAlbumCallback = new AddPostsToAlbumCallback() {
            @Override
            public void onAdd(ArrayList<Post> postList) {
                viewModel.addPostList(postList);
            }
        };
        albumPostListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Post>>() {
            @Override
            public void onChanged(ArrayList<Post> posts) {
                if(previewPostListAdapter == null){
                    previewPostListAdapter = new PreviewPostListAdapter(getContext(),posts,removingFieldView,postClickListener);
                    previewPostListAdapter.setDeletePostClickListener(deletePostListener);
                    previewPostListAdapter.setStartDragPostListener(dragPostListener);
                    albumPostListView.setAdapter(previewPostListAdapter);
                }
                else {
                    previewPostListAdapter.setPostList(posts);
                    previewPostListAdapter.notifyDataSetChanged();
                }
            }
        });
        albumNameLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String name) {
                albumNameView.setText(name);
            }
        });
        favoritesPostListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Post>>() {
            @Override
            public void onChanged(ArrayList<Post> posts) {
                addPostsToAlbumDialog.setPostList(posts);
            }
        });
        addPostsToAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPostsToAlbumDialog.show(addPostsToAlbumCallback);
            }
        });
        viewModel.loadData();
        return root;
    }
}