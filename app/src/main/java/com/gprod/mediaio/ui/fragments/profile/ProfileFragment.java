package com.gprod.mediaio.ui.fragments.profile;

import androidx.activity.OnBackPressedCallback;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.ProfileItemsListAdapter;
import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.enums.profile.ProfileItemTypes;
import com.gprod.mediaio.enums.profile.ProfileTypes;
import com.gprod.mediaio.enums.profile.ShareProfileTypes;
import com.gprod.mediaio.interfaces.adapters.DeletePostListener;
import com.gprod.mediaio.interfaces.adapters.EditProfileClickListener;
import com.gprod.mediaio.interfaces.adapters.PostClickListener;
import com.gprod.mediaio.interfaces.adapters.DragPostListener;
import com.gprod.mediaio.interfaces.fragments.profile.ShareProfileListener;
import com.gprod.mediaio.interfaces.repositories.user.SubscribeCallback;
import com.gprod.mediaio.interfaces.repositories.user.UnsubscribeCallback;
import com.gprod.mediaio.interfaces.services.database.DeletingPostCallback;
import com.gprod.mediaio.interfaces.services.nfc.ShareNfcCallback;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.profile.ProfileEditItem;
import com.gprod.mediaio.models.profile.ProfileInfoItem;
import com.gprod.mediaio.models.profile.ProfileItem;
import com.gprod.mediaio.models.profile.ProfilePostListItem;
import com.gprod.mediaio.models.profile.ProfileSubscribeItem;
import com.gprod.mediaio.models.profile.ProfileUnsubscribeItem;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;
import com.gprod.mediaio.services.popup.nfc.NfcSharingPopup;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private LiveData<User> userLiveData;
    private LiveData<ArrayList<Post>> postListLiveData;
    private LiveData<ProfileTypes> profileTypeLiveData;
    private RecyclerView profileItemListView;
    private ProfileItemsListAdapter profileItemListAdapter;
    private View navView;
    private NavController navController;
    private NavHostFragment navHostFragment;
    private ProfilePostListItem profilePostListItem;
    private ProfileInfoItem profileInfoItem;
    private ArrayList<ProfileItem> profileItems = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileItems.clear();
        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        View deletingFieldView = root.findViewById(R.id.deletingPostField);
        navView = getActivity().findViewById(R.id.nav_view);
        Animator showDeletingFieldAnimation = AnimatorInflater.loadAnimator(getContext(),R.animator.show_down);
        Animator hideDeletingFieldAnimation = AnimatorInflater.loadAnimator(getContext(),R.animator.hide_up);
        profileItemListView = root.findViewById(R.id.profileRootListView);
        profileItemListView.setLayoutManager(new LinearLayoutManager(getContext()));
        LayoutAnimationController profileItemsAnimation = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.profile_items_anim);
        profileItemListView.setLayoutAnimation(profileItemsAnimation);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userLiveData = viewModel.getUserLiveData();
        postListLiveData = viewModel.getPostListLiveData();
        profileTypeLiveData = viewModel.getProfileTypeLifeData();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(profileTypeLiveData.getValue() != null && profileTypeLiveData.getValue() == ProfileTypes.OTHER_USER_PROFILE){
                    viewModel.clearSelectedUser();
                    navView.setVisibility(View.VISIBLE);
                    navController.popBackStack();
                    this.remove();
                }
                else if (profileTypeLiveData.getValue() != null && profileTypeLiveData.getValue() == ProfileTypes.SELF_PROFILE) {
                    navController.popBackStack();
                    this.remove();
                }

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        PostClickListener postCLickListener = new PostClickListener() {
            @Override
            public void onClick(Post post) {
                if (post.getPostType().equals(PostTypes.IMAGE_POST)) {
                    onBackPressedCallback.remove();
                    viewModel.setSelectedPost(post);
                    navController.navigate(R.id.detailedPostFragment);
                }
                //TODO: add if statements for other post types
            }
        };
        EditProfileClickListener editProfileClickListener = new EditProfileClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressedCallback.remove();
                getActivity().findViewById(R.id.nav_view).setVisibility(View.GONE);
                navController.navigate(R.id.edit_profile_fragment);
            }
        };
        DragPostListener dragPostListener = new DragPostListener() {
            @Override
            public void onDrag(){
                deletingFieldView.bringToFront();
                deletingFieldView.setVisibility(View.VISIBLE);
                showDeletingFieldAnimation.setTarget(deletingFieldView);
                showDeletingFieldAnimation.start();
            }

            @Override
            public void onEnd() {
                hideDeletingFieldAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        deletingFieldView.setVisibility(View.INVISIBLE);
                    }
                });
                hideDeletingFieldAnimation.setTarget(deletingFieldView);
                hideDeletingFieldAnimation.start();
            }
        };
        DeletePostListener deletePostListener = new DeletePostListener() {
            @Override
            public void onDelete(Post post) {
                LoadingPopup.show(getContext());
                viewModel.deletePost(post, new DeletingPostCallback() {
                    @Override
                    public void onSuccess() {
                        LoadingPopup.hide(getContext());
                    }

                    @Override
                    public void onFailure() {
                        LoadingPopup.hide(getContext());
                        NotificationPopup.show(getContext(),true,"Произошла ошибка при попытке удалить пост");
                    }
                });
            }
        };
        View.OnClickListener subscribeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingPopup.show(getContext());
                viewModel.subscribe(new SubscribeCallback() {
                    @Override
                    public void onSuccess() {
                        profileItems.clear();
                        viewModel.reloadProfile(getContext());
                        LoadingPopup.hide(getContext());
                    }

                    @Override
                    public void onFailure() {
                        viewModel.reloadProfile(getContext());
                        LoadingPopup.hide(getContext());
                    }
                });

            }
        };
        View.OnClickListener unsubscribeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingPopup.show(getContext());
                viewModel.unsubscribe(new UnsubscribeCallback() {
                    @Override
                    public void onSuccess() {
                        profileItems.clear();
                        viewModel.reloadProfile(getContext());
                        LoadingPopup.hide(getContext());
                    }

                    @Override
                    public void onFailure() {
                        profileItems.clear();
                        viewModel.reloadProfile(getContext());
                        LoadingPopup.hide(getContext());
                    }
                });
            }
        };
        ShareProfileListener shareProfileListener = new ShareProfileListener() {
            @Override
            public void onShare(ShareProfileTypes shareProfileType) {
                if(shareProfileType == ShareProfileTypes.SHARE_TYPE_QR){
                    navController.navigate(R.id.qrCodeFragment);
                }
            }
        };
        profileItemListAdapter = new ProfileItemsListAdapter(getContext(),profileItems);
        postListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Post>>() {
            @Override
            public void onChanged(ArrayList<Post> posts) {
                if (profileTypeLiveData.getValue() != null) {
                    if(profilePostListItem != null && profileItems.contains(profilePostListItem)) {
                        profilePostListItem.updatePostList(getContext(),posts);
                    }
                    else {
                        profilePostListItem = new ProfilePostListItem(getContext(), posts, deletingFieldView, postCLickListener);
                        int markupIndex = Integer.parseInt(getResources().getString(R.string.profile_markup_post_list_item_index));
                        if (profileItems.size() > markupIndex) {
                            profileItems.add(markupIndex, profilePostListItem);
                        }
                        if (profileItems.size() <= markupIndex) {
                            profileItems.add(profilePostListItem);
                        }
                        if(profileTypeLiveData.getValue() == ProfileTypes.SELF_PROFILE){
                            profilePostListItem.setDeletePostClickListener(deletePostListener);
                            profilePostListItem.setStartDragPostListener(dragPostListener);
                        }
                        profileItemListAdapter.setProfileItems(profileItems);
                        profileItemListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        userLiveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(profileInfoItem != null && profileItems.contains(profileInfoItem)){
                    profileItems.remove(profileInfoItem);
                }
                profileInfoItem = new ProfileInfoItem(user);
                int markupIndex = Integer.parseInt(getResources().getString(R.string.profile_markup_info_item_index));
                if(profileItems.size() > markupIndex){
                    profileItems.add(markupIndex,profileInfoItem);
                }
                if(profileItems.size() <= markupIndex){
                    profileItems.add(profileInfoItem);
                }
                profileItemListAdapter.setProfileItems(profileItems);
                profileItemListAdapter.notifyDataSetChanged();
            }
        });
        profileTypeLiveData.observe(getViewLifecycleOwner(), new Observer<ProfileTypes>() {
            @Override
            public void onChanged(ProfileTypes profileTypes) {
                ProfileItem profileItem;
                if(profileTypes == ProfileTypes.SELF_PROFILE){
                    profileItem = new ProfileEditItem(editProfileClickListener,shareProfileListener);
                }
                else if(profileTypes == ProfileTypes.OTHER_USER_PROFILE) {
                    navView.setVisibility(View.GONE);
                    if(!viewModel.getSubscribeStatus()){
                        profileItem = new ProfileSubscribeItem(subscribeClickListener);
                    }
                    else {
                        profileItem = new ProfileUnsubscribeItem(unsubscribeClickListener);
                    }
                }
                else {
                    profileItem = new ProfileEditItem(editProfileClickListener,shareProfileListener);
                }
                int markupIndex = getMarkupIndex(profileItem);
                Log.d("MY LOGS","Markup index >>: " + markupIndex);
                if(!containsEditItem(profileItems)) {
                    if (profileItems.size() > markupIndex) {
                        profileItems.add(markupIndex, profileItem);
                    }
                    if (profileItems.size() <= markupIndex) {
                        profileItems.add(profileItem);
                    }
                }
                profileItemListAdapter.setProfileItems(profileItems);
                profileItemListAdapter.notifyDataSetChanged();
            }
        });
        profileItemListView.setAdapter(profileItemListAdapter);
        viewModel.loadProfile(getContext());
        return root;
    }
    public int getMarkupIndex(ProfileItem profileItem){
        int markupIndex = 0;
        if(profileItem.getProfileItemType() == ProfileItemTypes.PROFILE_EDIT_ITEM){
            markupIndex = Integer.parseInt(getResources().getString(R.string.profile_markup_edit_item_index));
        }
        if(profileItem.getProfileItemType() == ProfileItemTypes.PROFILE_INFO_ITEM){
            markupIndex = Integer.parseInt(getResources().getString(R.string.profile_markup_info_item_index));
        }
        if(profileItem.getProfileItemType() == ProfileItemTypes.PROFILE_POST_LIST_ITEM){
            markupIndex = Integer.parseInt(getResources().getString(R.string.profile_markup_post_list_item_index));
        }
        if(profileItem.getProfileItemType() == ProfileItemTypes.PROFILE_SUBSCRIBE_ITEM){
            markupIndex = Integer.parseInt(getResources().getString(R.string.profile_markup_subscribe_item_index));
        }
        if(profileItem.getProfileItemType() == ProfileItemTypes.PROFILE_UNSUBSCRIBE_ITEM){
            markupIndex = Integer.parseInt(getResources().getString(R.string.profile_markup_unsubscribe_item_index));
        }
        return markupIndex;
    }
    public boolean containsEditItem(ArrayList<ProfileItem> profileItems) {
        for(ProfileItem item : profileItems){
            if(item instanceof ProfileEditItem){
                return true;
            }
        }
        return false;
    }
}