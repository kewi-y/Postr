package com.gprod.mediaio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.profile.ProfileItemTypes;
import com.gprod.mediaio.interfaces.adapters.ProfileItemViewTypes;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.profile.ProfileEditItem;
import com.gprod.mediaio.models.profile.ProfileInfoItem;
import com.gprod.mediaio.models.profile.ProfileItem;
import com.gprod.mediaio.models.profile.ProfilePostListItem;
import com.gprod.mediaio.models.profile.ProfileSubscribeItem;
import com.gprod.mediaio.models.profile.ProfileUnsubscribeItem;

import java.util.ArrayList;

public class ProfileItemsListAdapter extends RecyclerView.Adapter<ProfileItemsListAdapter.ProfileItemHolder> {
    private ArrayList<ProfileItem> profileItems;
    private LayoutInflater inflater;

    public ProfileItemsListAdapter(Context context, ArrayList<ProfileItem>profileItems){
        inflater = LayoutInflater.from(context);
        this.profileItems = profileItems;
    }
    public void setProfileItems(ArrayList<ProfileItem> profileItems){
        this.profileItems = profileItems;
    }
    @Override
    public int getItemViewType(int position) {
        ProfileItem item = profileItems.get(position);
        if(item.getProfileItemType().equals(ProfileItemTypes.PROFILE_INFO_ITEM)){
            return ProfileItemViewTypes.PROFILE_ITEM_TYPE_INFO;
        }
        if(item.getProfileItemType().equals(ProfileItemTypes.PROFILE_EDIT_ITEM)){
            return ProfileItemViewTypes.PROFILE_ITEM_TYPE_EDIT;
        }
        if(item.getProfileItemType().equals(ProfileItemTypes.PROFILE_POST_LIST_ITEM)){
            return ProfileItemViewTypes.PROFILE_ITEM_TYPE_POST_LIST;
        }
        if(item.getProfileItemType().equals(ProfileItemTypes.PROFILE_SUBSCRIBE_ITEM)){
            return ProfileItemViewTypes.PROFILE_ITEM_TYPE_SUBSCRIBE;
        }
        if(item.getProfileItemType().equals(ProfileItemTypes.PROFILE_UNSUBSCRIBE_ITEM)){
            return ProfileItemViewTypes.PROFILE_ITEM_TYPE_UNSUBSCRIBE;
        }
        else {
            return ProfileItemViewTypes.PROFILE_ITEM_TYPE_NOT_HAVE;
        }
    }

    @NonNull
    @Override
    public ProfileItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == ProfileItemViewTypes.PROFILE_ITEM_TYPE_INFO){
            view = inflater.inflate(R.layout.profile_info_item,parent,false);
            return new ProfileInfoItemHolder(view);
        }
        if(viewType == ProfileItemViewTypes.PROFILE_ITEM_TYPE_EDIT){
            view = inflater.inflate(R.layout.profile_edit_item,parent,false);
            return new ProfileEditItemHolder(view);
        }
        if(viewType == ProfileItemViewTypes.PROFILE_ITEM_TYPE_POST_LIST){
            view = inflater.inflate(R.layout.profile_posts_item,parent,false);
            return new ProfilePostsItemHolder(view);
        }
        if(viewType == ProfileItemViewTypes.PROFILE_ITEM_TYPE_SUBSCRIBE){
            view = inflater.inflate(R.layout.profile_subscribe_item,parent,false);
            return new ProfileSubscribeItemHolder(view);
        }
        if(viewType == ProfileItemViewTypes.PROFILE_ITEM_TYPE_UNSUBSCRIBE){
            view = inflater.inflate(R.layout.profile_unsubscribe_item,parent,false);
            return new ProfileUnsubscribeItemHolder(view);
        }
        else{
            return null;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ProfileItemHolder holder, int position) {
        ProfileItem profileItem;
        profileItem = profileItems.get(position);
        if(holder instanceof ProfilePostsItemHolder){
            ((ProfilePostsItemHolder) holder).setAdapter(((ProfilePostListItem) profileItem).getPostListAdapter());
        }
        if(holder instanceof ProfileInfoItemHolder){
            ((ProfileInfoItemHolder) holder).setData(((ProfileInfoItem) profileItem).getUser());
        }
        if(holder instanceof ProfileEditItemHolder){
            ((ProfileEditItemHolder) holder).setClickListener(((ProfileEditItem) profileItem).getClickListener());
        }
        if(holder instanceof ProfileSubscribeItemHolder){
            ((ProfileSubscribeItemHolder) holder).setClickListener(((ProfileSubscribeItem) profileItem).getOnClickListener());
        }
        if(holder instanceof ProfileUnsubscribeItemHolder){
            ((ProfileUnsubscribeItemHolder) holder).setClickListener(((ProfileUnsubscribeItem) profileItem).getOnClickListener());
        }
    }

    @Override
    public int getItemCount() {
        return profileItems.size();
    }

    abstract class ProfileItemHolder extends RecyclerView.ViewHolder{
        public ProfileItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    class ProfileInfoItemHolder extends ProfileItemHolder {
        private TextView profilenameTextView,subscriptionsCountTextView,
                subscribersCountTextView,publicationCountTextView,
                usernameTextView,bioTextView;
        SimpleDraweeView profilePhotoView;
        public ProfileInfoItemHolder(@NonNull View itemView) {
            super(itemView);
            profilenameTextView = itemView.findViewById(R.id.profilenameTextView);
            subscriptionsCountTextView = itemView.findViewById(R.id.subscriptionsCountTextView);
            subscribersCountTextView = itemView.findViewById(R.id.subscribersCountTextView);
            publicationCountTextView = itemView.findViewById(R.id.publicationCountTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            bioTextView = itemView.findViewById(R.id.bioTextView);
            profilePhotoView = itemView.findViewById(R.id.profilePhotoView);
        }
        public void setData(User user){
            if(user != null) {
                profilenameTextView.setText(user.getProfilename());
                subscriptionsCountTextView.setText(String.valueOf(user.getSubscriptionsCount()));
                subscribersCountTextView.setText(String.valueOf(user.getSubscribersCount()));
                publicationCountTextView.setText(String.valueOf(user.getPublicationsCount()));
                usernameTextView.setText(user.getUsername());
                bioTextView.setText(user.getBio());
                profilePhotoView.setImageURI(user.getProfilePhotoDownloadUri());
            }
        }

    }
    class ProfilePostsItemHolder extends ProfileItemHolder {
        private RecyclerView postListView;
        public ProfilePostsItemHolder(@NonNull View itemView) {
            super(itemView);
            postListView = itemView.findViewById(R.id.postListView);
            postListView.setLayoutManager(new GridLayoutManager(itemView.getContext(),
                    Integer.parseInt(itemView.getResources().getString(R.string.profile_posts_span_count))));
        }
        public void setAdapter(PreviewPostListAdapter previewPostListAdapter){
            if(previewPostListAdapter != null) {
                postListView.setAdapter(previewPostListAdapter);
            }
        }
    }

    class ProfileEditItemHolder extends ProfileItemHolder{
        private MaterialButton editProfileButton;
        public ProfileEditItemHolder(@NonNull View itemView) {
            super(itemView);
            editProfileButton = itemView.findViewById(R.id.editProfileButton);
        }
        public void setClickListener(View.OnClickListener onClickListener){
            editProfileButton.setOnClickListener(onClickListener);
        }
    }
    class ProfileSubscribeItemHolder extends ProfileItemHolder{
        private MaterialButton subscribeButton;
        public ProfileSubscribeItemHolder(@NonNull View itemView) {
            super(itemView);
            subscribeButton = itemView.findViewById(R.id.subscribeButton);
        }
        public void setClickListener(View.OnClickListener clickListener){
            subscribeButton.setOnClickListener(clickListener);
        }
    }
    class ProfileUnsubscribeItemHolder extends ProfileItemHolder{
        private MaterialButton unsubscribeButton;
        public ProfileUnsubscribeItemHolder(@NonNull View itemView) {
            super(itemView);
            unsubscribeButton = itemView.findViewById(R.id.unsubscribeButton);
        }
        public void setClickListener(View.OnClickListener onClickListener){
            unsubscribeButton.setOnClickListener(onClickListener);
        }
    }
}
