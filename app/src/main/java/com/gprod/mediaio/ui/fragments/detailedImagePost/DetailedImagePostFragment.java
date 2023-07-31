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
import com.gprod.mediaio.interfaces.dialogs.comments.AddCommentListener;
import com.gprod.mediaio.interfaces.fragments.detailedImagePost.TagClickListener;
import com.gprod.mediaio.interfaces.services.database.GettingTagByStringTagCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.Tag;
import com.gprod.mediaio.ui.dialogs.comments.CommentsDialog;

public class DetailedImagePostFragment extends Fragment {

    private DetailedImagePostViewModel viewModel;
    private ImageButton detailedImagePostLikeButton,detailedImagePostCommentButton;
    private LiveData<User> authorLiveData;
    private LiveData<ImagePost> postLiveData;
    private LiveData<Boolean> userLikeLiveData;
    private CommentsDialog commentsDialog;
    private ImageButton detailedImagePostAddToFavoritesButton;
    private NavController navController;
    private NavHostFragment navHostFragment;
    private TypedValue typedValue;
    private SimpleDraweeView detailedImagePostView, detailedImagePostProfilePhotoView;
    private TextView detailedImagePostNameView,
            detailedImagePostTagView,
            detailedImagePostLikeCountTextView,
            detailedImagePostDescriptionView,
            detailedImagePostCommentCountView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detailed_image_post_fragment, container, false);
        viewModel = new ViewModelProvider(this).get(DetailedImagePostViewModel.class);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        authorLiveData = viewModel.getAuthorLiveData();
        postLiveData = viewModel.getPostLiveData();
        userLikeLiveData = viewModel.getUserLikeLiveData();
        detailedImagePostView = root.findViewById(R.id.detailedImagePostView);
        detailedImagePostProfilePhotoView = root.findViewById(R.id.detailedImagePostProfilePhotoView);
        detailedImagePostNameView = root.findViewById(R.id.detailedImagePostNameView);
        detailedImagePostTagView = root.findViewById(R.id.detailedImagePostTagView);
        detailedImagePostCommentCountView = root.findViewById(R.id.detailedImagePostCommentCountView);
        detailedImagePostCommentButton = root.findViewById(R.id.detailedImagePostCommentButton);
        detailedImagePostDescriptionView = root.findViewById(R.id.detailedImagePostDescriptionView);
        detailedImagePostLikeButton = root.findViewById(R.id.detailedImagePostLikeButton);
        detailedImagePostAddToFavoritesButton = root.findViewById(R.id.detailedImagePostAddToFavoritesButton);
        detailedImagePostLikeCountTextView = root.findViewById(R.id.detailedImagePostLikeCountTextView);
        commentsDialog = CommentsDialog.getInstance(getContext(),getActivity());
        typedValue = new TypedValue();
        authorLiveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                detailedImagePostNameView.setText(user.getUsername());
                detailedImagePostTagView.setText(user.getProfilename());
                detailedImagePostProfilePhotoView.setImageURI(user.getProfilePhotoDownloadUri());
            }
        });
        if(viewModel.checkOnFavorites()){
            getContext().getTheme().resolveAttribute(R.attr.colorSecondary,typedValue,true);
            detailedImagePostAddToFavoritesButton.setColorFilter(typedValue.data);
        }
        TagClickListener tagClickListener = new TagClickListener() {
            @Override
            public void onClick(String tag) {
                viewModel.getTagByStringTag(tag, new GettingTagByStringTagCallback() {
                    @Override
                    public void onResult(Tag tag) {
                        viewModel.selectTag(tag);
                        navController.navigate(R.id.detailedTagFragment);
                    }

                    @Override
                    public void onFailure() {
                        //TODO:Notify userF
                    }
                });
            }
        };
        postLiveData.observe(getViewLifecycleOwner(), new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                detailedImagePostView.setImageURI(imagePost.getPostImageDownloadUriList().get(0));
                SpannableString spannableDescriptionString = getSpannableStringForDescription(imagePost.getDescription(),tagClickListener);
                detailedImagePostDescriptionView.setMovementMethod(LinkMovementMethod.getInstance());
                detailedImagePostDescriptionView.setText(spannableDescriptionString, TextView.BufferType.SPANNABLE);
                if(imagePost.getLikes() != null){
                    detailedImagePostLikeCountTextView.setText(String.valueOf(imagePost.getLikes().size()));
                    viewModel.checkOnLike();
                }
                else {
                    detailedImagePostLikeCountTextView.setText(getResources().getString(R.string.title_default_like_count));
                    viewModel.checkOnLike();
                }
                if(imagePost.getComments() != null){
                    if(imagePost.getComments().size() != 0) {
                        detailedImagePostCommentCountView.setText(String.valueOf(imagePost.getComments().size()));
                        commentsDialog.updateCommentList(getContext(), imagePost.getComments());
                    }
                    else {
                        detailedImagePostCommentCountView.setText(getResources().getString(R.string.title_default_comments_count));
                        commentsDialog.updateCommentList(getContext(),imagePost.getComments());
                    }
                }
                else {
                    detailedImagePostCommentCountView.setText(getResources().getString(R.string.title_default_comments_count));
                    commentsDialog.updateCommentList(getContext(),imagePost.getComments());
                }
            }
        });
        userLikeLiveData.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    getContext().getTheme().resolveAttribute(R.attr.colorSecondary,typedValue,true);
                    detailedImagePostLikeButton.setColorFilter(typedValue.data);
                }
                else{
                    getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary,typedValue,true);
                    detailedImagePostLikeButton.setColorFilter(typedValue.data);
                }
            }
        });
        detailedImagePostLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailedImagePostLikeButton.setClickable(false);
                if(userLikeLiveData.getValue()){
                    viewModel.removeLike(new UpdatingPostCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            //TODO: Show some animation
                            detailedImagePostLikeButton.setClickable(true);
                        }

                        @Override
                        public void onFailure() {
                            //TODO:Notify
                        }
                    });
                }
                else {
                    viewModel.addLike(new UpdatingPostCallback() {
                        @Override
                        public void onSuccess(Post post) {
                            //TODO: Show some animation
                            detailedImagePostLikeButton.setClickable(true);
                        }

                        @Override
                        public void onFailure() {
                            //TODO:Notify
                        }
                    });
                }
            }
        });
        detailedImagePostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsDialog.show(getContext(), new AddCommentListener() {
                    @Override
                    public void onAddComment(String commentText) {
                        viewModel.addComment(commentText, new UpdatingPostCallback() {
                            @Override
                            public void onSuccess(Post post) {

                            }

                            @Override
                            public void onFailure() {
                                //TODO: Notify
                            }
                        });
                    }
                });
            }
        });
        detailedImagePostAddToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!viewModel.checkOnFavorites()){
                    viewModel.addPostToFavorites(new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            getContext().getTheme().resolveAttribute(R.attr.colorSecondary,typedValue,true);
                            detailedImagePostAddToFavoritesButton.setColorFilter(typedValue.data);
                        }

                        @Override
                        public void onFailure(String textError) {
                            //TODO: Notify
                        }
                    });
                }
                else {
                    viewModel.removePostFromFavorites(new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary,typedValue,true);
                            detailedImagePostAddToFavoritesButton.setColorFilter(typedValue.data);
                        }

                        @Override
                        public void onFailure(String textError) {

                        }
                    });
                }
            }
        });
        viewModel.loadData();
        return root;
    }

    private SpannableString getSpannableStringForDescription(String text, TagClickListener tagClickListener){
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
                            Log.d("MY LOGS","on span click >>: " + word);
                            tagClickListener.onClick(word);
                        }
                    };
                    spannableDescriptionString.setSpan(clickableSpan,start,end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableDescriptionString;
    }
}