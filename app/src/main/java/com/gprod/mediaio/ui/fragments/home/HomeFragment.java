package com.gprod.mediaio.ui.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.HomeItemListAdapter;
import com.gprod.mediaio.interfaces.adapters.AddCommentClickListener;
import com.gprod.mediaio.interfaces.adapters.AddToFavoritesClickListener;
import com.gprod.mediaio.interfaces.adapters.LikeClickListener;
import com.gprod.mediaio.interfaces.adapters.PostAuthorClickListener;
import com.gprod.mediaio.interfaces.home.StoryItemClickListener;
import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.home.FeedHomeItem;
import com.gprod.mediaio.models.home.HomeItem;
import com.gprod.mediaio.models.home.StoriesHomeItem;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.models.story.StoryItem;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.ui.dialogs.comments.CommentsDialog;
import com.gprod.mediaio.ui.dialogs.story.StoryViewDialog;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private LiveData<ArrayList<StoryItem>> storyItemListLiveData;
    private LiveData<ArrayList<PostItem>> postItemListLiveData;
    private RecyclerView homeItemListView;
    private ArrayList<HomeItem> homeItemList;
    private HomeItemListAdapter homeItemListAdapter;
    private StoryViewDialog storyViewDialog;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.init(getResources().getString(R.string.story_api_url));
        View root = inflater.inflate(R.layout.home_fragment,container,false);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        homeItemList = new ArrayList<>();
        postItemListLiveData = viewModel.getPostItemListLiveData();
        homeItemListView = root.findViewById(R.id.homeItemListView);
        storyViewDialog = StoryViewDialog.getInstance(requireContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        homeItemListView.setLayoutManager(linearLayoutManager);
        CommentsDialog commentsDialog = CommentsDialog.getInstance(getContext(),getActivity());
        FeedHomeItem feedHomeItem = new FeedHomeItem(getContext(),postItemListLiveData.getValue(),commentsDialog);
        PostAuthorClickListener postAuthorClickListener = new PostAuthorClickListener() {
            @Override
            public void onClick(String authorId) {
                viewModel.selectUser(authorId, new SelectUserCallback() {
                    @Override
                    public void onSelected() {
                        navController.navigate(R.id.navigation_profile);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        };
        StoryItemClickListener storyItemClickListener = new StoryItemClickListener() {
            @Override
            public void onClick(StoryItem storyItem) {
                Log.d("MY LOGS","On click on story item");
                storyViewDialog.setStories(storyItem);
                storyViewDialog.show();
            }
        };
        LikeClickListener likeClickListener = new LikeClickListener() {
            @Override
            public void onClick(View itemView, PostItem postItem) {
                if(postItem.isOnLike()){
                    viewModel.removeLike(postItem.getPost(), new UpdatingPostCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            postItem.setPost(post);
                            postItem.setOnLike(false);
                            feedHomeItem.updatePostItem(getContext(),postItem);
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
                            feedHomeItem.updatePostItem(getContext(),postItem);
                        }

                        @Override
                        public void onFailure() {
                            //TODO: Notify
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
                        feedHomeItem.updatePostItem(getContext(),postItem);
                    }

                    @Override
                    public void onFailure() {
                        //TODO: notify User
                    }
                });
            }
        };
        AddToFavoritesClickListener addToFavoritesClickListener = new AddToFavoritesClickListener() {
            @Override
            public void onCLick(PostItem postItem) {
                if(postItem.isOnFavorites()){
                    viewModel.removeFromFavorites(postItem.getPost(), new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            postItem.setOnFavorites(false);
                            feedHomeItem.updatePostItem(getContext(),postItem);
                        }

                        @Override
                        public void onFailure(String textError) {
                            //TODO: notify
                        }
                    });
                }
                else{
                    viewModel.addToFavorites(postItem.getPost(), new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            postItem.setOnFavorites(true);
                            feedHomeItem.updatePostItem(getContext(),postItem);
                        }

                        @Override
                        public void onFailure(String textError) {
                            //TODO:notify
                        }
                    });
                }
            }
        };
        StoriesHomeItem storiesHomeItem = new StoriesHomeItem(getContext(),storyItemClickListener);
        homeItemList.add(storiesHomeItem);
        homeItemList.add(feedHomeItem);
        feedHomeItem.setAddCommentClickListener(addCommentClickListener);
        feedHomeItem.setAddToFavoritesClickListener(addToFavoritesClickListener);
        feedHomeItem.setLikeClickListener(likeClickListener);
        feedHomeItem.setPostAuthorClickListener(postAuthorClickListener);
        storyItemListLiveData = viewModel.getStoryItemListLiveData();
        storyItemListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<StoryItem>>() {
            @Override
            public void onChanged(ArrayList<StoryItem> storyItems) {
                storiesHomeItem.setStoryItems(getContext(),storyItems);
            }
        });
        postItemListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<PostItem>>() {
            @Override
            public void onChanged(ArrayList<PostItem> postItems) {
                feedHomeItem.updatePostsItems(postItems);
            }
        });
        homeItemListAdapter = new HomeItemListAdapter(getContext(), homeItemList);
        homeItemListView.setAdapter(homeItemListAdapter);
        viewModel.loadStories();
        viewModel.loadPosts(getContext(),1);
        return root;
    }
}