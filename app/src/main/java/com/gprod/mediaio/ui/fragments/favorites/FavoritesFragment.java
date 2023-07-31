package com.gprod.mediaio.ui.fragments.favorites;

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
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.AlbumPagerItemListAdapter;
import com.gprod.mediaio.adapters.PostListAdapter;
import com.gprod.mediaio.interfaces.adapters.AddCommentClickListener;
import com.gprod.mediaio.interfaces.adapters.AddToFavoritesClickListener;
import com.gprod.mediaio.interfaces.adapters.AlbumClickListener;
import com.gprod.mediaio.interfaces.adapters.LikeClickListener;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.models.album.Album;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.ui.dialogs.comments.CommentsDialog;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel viewModel;
    private RecyclerView favoritesPostListView;
    private PostListAdapter favoritesPostListAdapter;
    private AlbumPagerItemListAdapter albumPagerItemListAdapter;
    private ViewPager2 albumItemListPager;
    private TabLayout albumItemListTabLayout;
    private ImageButton addAlbumButton;
    private LiveData<ArrayList<PostItem>> favoritesPostItemListLiveData;
    private LiveData<ArrayList<Album>> albumListLiveData;
    private NavController navController;
    private NavHostFragment navHostFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        View root = inflater.inflate(R.layout.favorites_fragment, container, false);
        favoritesPostListView = root.findViewById(R.id.favoritesPostListView);
        albumItemListPager = root.findViewById(R.id.albumItemListPager);
        albumItemListTabLayout = root.findViewById(R.id.albumItemListTabLayout);
        addAlbumButton = root.findViewById(R.id.addAlbumButton);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        albumPagerItemListAdapter = new AlbumPagerItemListAdapter(getContext());
        albumItemListPager.setAdapter(albumPagerItemListAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(albumItemListTabLayout, albumItemListPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {}
        });
        CommentsDialog commentsDialog = CommentsDialog.getInstance(getContext(),getActivity());
        favoritesPostItemListLiveData = viewModel.getFavoritesPostItemListLiveData();
        favoritesPostListAdapter = new PostListAdapter(getContext(),favoritesPostItemListLiveData.getValue(),commentsDialog);
        favoritesPostListView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesPostListView.setAdapter(favoritesPostListAdapter);
        favoritesPostListView.setHasFixedSize(true);
        albumListLiveData = viewModel.getAlbumListLiveData();
        ((SimpleItemAnimator) favoritesPostListView.getItemAnimator()).setSupportsChangeAnimations(false);
        AlbumClickListener albumClickListener  = new AlbumClickListener() {
            @Override
            public void onClick(Album album) {
                viewModel.selectAlbum(album);
                navController.navigate(R.id.albumFragment);
            }
        };
        LikeClickListener likeClickListener = new LikeClickListener() {
            @Override
            public void onClick(View itemView, PostItem postItem) {
                if(postItem.isOnLike()){
                    viewModel.removeLike(postItem.getPost(), new UpdatingPostCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            postItem.setOnLike(false);
                            postItem.setPost(post);
                            favoritesPostListAdapter.updateItem(getContext(),postItem);
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
                            favoritesPostListAdapter.updateItem(getContext(),postItem);
                        }

                        @Override
                        public void onFailure() {
                            //TODO:Notify
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
                        favoritesPostListAdapter.updateItem(getContext(),postItem);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        };
        AddToFavoritesClickListener addToFavoritesClickListener = new AddToFavoritesClickListener() {
            @Override
            public void onCLick(PostItem postItem) {
                viewModel.removePostFromFavorites(postItem);
            }
        };
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.addAlbumFragment);
            }
        });
        albumPagerItemListAdapter.setAlbumClickListener(albumClickListener);
        favoritesPostListAdapter.setAddToFavoritesClickListener(addToFavoritesClickListener);
        favoritesPostListAdapter.setAddCommentClickListener(addCommentClickListener);
        favoritesPostListAdapter.setLikeClickListener(likeClickListener);
        favoritesPostItemListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<PostItem>>() {
            @Override
            public void onChanged(ArrayList<PostItem> postItems) {
                favoritesPostListAdapter.updatePostItemList(postItems);
            }
        });
        albumListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Album>>() {
            @Override
            public void onChanged(ArrayList<Album> albums) {
                albumPagerItemListAdapter.updateAlbumList(albums);
                tabLayoutMediator.attach();
            }
        });
        viewModel.loadData(getContext());
        return root;
    }

}