package com.gprod.mediaio.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.interfaces.adapters.AddCommentClickListener;
import com.gprod.mediaio.interfaces.adapters.AddToFavoritesClickListener;
import com.gprod.mediaio.interfaces.adapters.LikeClickListener;
import com.gprod.mediaio.interfaces.adapters.PostAuthorClickListener;
import com.gprod.mediaio.interfaces.adapters.PostViewTypes;
import com.gprod.mediaio.interfaces.dialogs.comments.AddCommentListener;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.PostItem;
import com.gprod.mediaio.ui.dialogs.comments.CommentsDialog;

import java.net.ConnectException;
import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostHolder> {
    private ArrayList<PostItem> postItemList;
    private LayoutInflater inflater;
    private CommentsDialog commentsDialog;
    private LikeClickListener likeClickListener;
    private AddCommentClickListener addCommentClickListener;
    private AddToFavoritesClickListener addToFavoritesClickListener;
    private PostAuthorClickListener postAuthorClickListener;
    public PostListAdapter(Context context, ArrayList<PostItem>postItemList, CommentsDialog commentsDialog){
        this.postItemList = postItemList;
        this.commentsDialog = commentsDialog;
        inflater = LayoutInflater.from(context);
    }
    public void setLikeClickListener(LikeClickListener likeClickListener){
        this.likeClickListener = likeClickListener;
    }
    public void setAddCommentClickListener(AddCommentClickListener addCommentClickListener){
        this.addCommentClickListener = addCommentClickListener;
    }
    public void setAddToFavoritesClickListener(AddToFavoritesClickListener addToFavoritesClickListener){
        this.addToFavoritesClickListener = addToFavoritesClickListener;
    }
    public void setPostAuthorClickListener(PostAuthorClickListener postAuthorClickListener){
        this.postAuthorClickListener = postAuthorClickListener;
    }
    public void updatePostItemList(ArrayList<PostItem> postItemList){
        this.postItemList = postItemList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        PostItem postItem = postItemList.get(position);
        Post post = postItem.getPost();
        if (post.getPostType().equals(PostTypes.IMAGE_POST)) {
            return PostViewTypes.IMAGE_POST;
        } else if (post.getPostType().equals(PostTypes.VIDEO_POST)) {
            return PostViewTypes.VIDEO_POST;
        } else if (post.getPostType().equals(PostTypes.OTHER_SOURCE_VIDEO_POST)) {
            return PostViewTypes.OTHER_SOURCE_VIDEO_POST;
        } else {
            return PostViewTypes.VIEW_TYPE_NOT_HAVE;
        }
    }
    public void updateItem(Context context,PostItem postItem){
        if(postItemList.contains(postItem)) {
            int itemIndex = postItemList.indexOf(postItem);
            postItemList.set(itemIndex,postItem);
            notifyItemChanged(itemIndex);
            commentsDialog.updateCommentList(context,postItem.getPost().getComments());
        }
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == PostViewTypes.IMAGE_POST){
            view = inflater.inflate(R.layout.image_post_item,parent,false);
            return new ImagePostHolder(view);
        }
        if(viewType == PostViewTypes.VIDEO_POST){
            //TODO: add layout for other posts type
        }
        if(viewType == PostViewTypes.OTHER_SOURCE_VIDEO_POST){

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        if(holder instanceof ImagePostHolder){
            ((ImagePostHolder)holder).setData(postItemList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(postItemList != null) {
            return postItemList.size();
        }
        else {
            return 0;
        }
    }

    abstract class PostHolder extends RecyclerView.ViewHolder {
        View itemView;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

    }

    class ImagePostHolder extends PostHolder{
        private SimpleDraweeView profilePhotoView;
        private TextView descriptionView,likeCountView,commentsCountView,usernameView,accountnameView;
        private ImageButton likeButton,commentButton,addToFavoritesFragment;
        private ViewPager2 imageItemPager;
        private TabLayout imageItemTabLayout;
        private ImageItemListAdapter imageItemListAdapter;
        public ImagePostHolder(@NonNull View itemView) {
            super(itemView);
            imageItemPager = itemView.findViewById(R.id.imageItemViewPager);
            imageItemTabLayout = itemView.findViewById(R.id.imageItemTabLayout);
            descriptionView = itemView.findViewById(R.id.imagePostDescriptionView);
            likeCountView = itemView.findViewById(R.id.imagePostLikeCountTextView);
            commentsCountView = itemView.findViewById(R.id.imagePostCommentCountView);
            likeButton = itemView.findViewById(R.id.imagePostLikeButton);
            commentButton = itemView.findViewById(R.id.imagePostCommentButton);
            usernameView = itemView.findViewById(R.id.imagePostNameView);
            accountnameView = itemView.findViewById(R.id.imagePostTagView);
            profilePhotoView = itemView.findViewById(R.id.imagePostProfilePhotoView);
            addToFavoritesFragment = itemView.findViewById(R.id.imagePostAddToFavoritesButton);
            imageItemListAdapter = new ImageItemListAdapter(itemView.getContext());
        }
        public void setData(PostItem postItem) {
            ImagePost post = (ImagePost) postItem.getPost();
            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(imageItemTabLayout, imageItemPager, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                }
            });
            imageItemPager.setAdapter(imageItemListAdapter);
            imageItemListAdapter.setImageDownloadUriList(post.getPostImageDownloadUriList());
            tabLayoutMediator.attach();
            profilePhotoView.setImageURI(postItem.getUserProfilePhotoDownloadUri());
            displayGeneralData(descriptionView,likeCountView,commentsCountView,usernameView,accountnameView,postItem);
            setGeneralListeners(postItem,this.itemView,likeButton,commentButton,addToFavoritesFragment,profilePhotoView);
        }
    }
    class VideoPostHolder extends PostHolder{

        public VideoPostHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    class OtherSourceVideoPostHolder extends PostHolder{

        public OtherSourceVideoPostHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private SpannableString getSpannableStringForDescription(String text){
        SpannableString spannableDescriptionString = new SpannableString(text);
        String[] splitDescriptionString = text.split(" ");
        int end = 0;
        for(String word : splitDescriptionString){
            end += word.length() + 1;
            if(word.contains("#") && !word.isEmpty()){
                Character wordFirstChar = word.charAt(0);
                Character hashtagCharacter = "#".charAt(0);
                if(wordFirstChar.equals(hashtagCharacter)){
                    Log.d("MY LOGS","span added");
                    int start = end - word.length() - 1;
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            //TODO: notify parent fragment with listener
                        }
                    };
                    spannableDescriptionString.setSpan(clickableSpan,start,end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableDescriptionString;
    }
    private void displayGeneralData(TextView descriptionView,TextView likeCountView,
                             TextView commentsCountView,TextView usernameView,
                             TextView accountnameView,PostItem postItem){
        Post post = postItem.getPost();
        SpannableString descriptionString = getSpannableStringForDescription(post.getDescription());
        descriptionView.setText(descriptionString, TextView.BufferType.SPANNABLE);
        if(post.getLikes() != null){
            likeCountView.setText(String.valueOf(post.getLikes().size()));
        }
        if(post.getComments() != null){
            commentsCountView.setText(String.valueOf(post.getComments().size()));
        }
        usernameView.setText(postItem.getUsername());
        accountnameView.setText(postItem.getAccountname());
    }

    private void setGeneralListeners(PostItem postItem,View itemView,ImageButton likeButton,ImageButton commentButton,ImageButton addToFavoritesButton,View profilePhotoView){
        Post post = postItem.getPost();
        TypedValue typedValue = new TypedValue();
        if(postItem.isOnLike()){
            itemView.getContext().getTheme().resolveAttribute(R.attr.colorSecondary,typedValue,true);
            likeButton.setColorFilter(typedValue.data);
        }
        else {
            itemView.getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary,typedValue,true);
            likeButton.setColorFilter(typedValue.data);
        }
        if(postItem.isOnFavorites()){
            itemView.getContext().getTheme().resolveAttribute(R.attr.colorSecondary,typedValue,true);
            addToFavoritesButton.setColorFilter(typedValue.data);
        }
        else {
            itemView.getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary,typedValue,true);
            addToFavoritesButton.setColorFilter(typedValue.data);
        }
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsDialog.updateCommentList(itemView.getContext(),post.getComments());
                commentsDialog.show(itemView.getContext(), new AddCommentListener() {
                    @Override
                    public void onAddComment(String commentText) {
                        if(addCommentClickListener != null) {
                            addCommentClickListener.onCLick(postItem, commentText);
                        }
                    }
                });
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(likeClickListener != null) {
                    likeClickListener.onClick(itemView, postItem);
                }
            }
        });
        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addToFavoritesClickListener != null){
                    addToFavoritesClickListener.onCLick(postItem);
                }
            }
        });
        profilePhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postAuthorClickListener != null){
                    postAuthorClickListener.onClick(postItem.getPost().getAuthorId());
                }
            }
        });
    }
}
