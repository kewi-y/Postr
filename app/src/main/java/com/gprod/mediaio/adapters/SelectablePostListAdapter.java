package com.gprod.mediaio.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.interfaces.adapters.PostViewTypes;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.post.Post;

import java.util.ArrayList;

public class SelectablePostListAdapter extends RecyclerView.Adapter<SelectablePostListAdapter.SelectablePostHolder> {
    private ArrayList<Post> selectedPostList = new ArrayList<>();
    private ArrayList<Post> postList;
    private LayoutInflater inflater;
    private Animator showDownAnimation;
    private Animator hideUpAnimation;

    public ArrayList<Post> getSelectedPostList() {
        return selectedPostList;
    }
    public void clearSelectedPostList(){
        selectedPostList.clear();
    }
    public SelectablePostListAdapter(Context context){
        inflater = LayoutInflater.from(context);
        showDownAnimation = AnimatorInflater.loadAnimator(context,R.animator.show_down);
        hideUpAnimation = AnimatorInflater.loadAnimator(context,R.animator.hide_up);
    }
    public void updatePostList(ArrayList<Post> postList){
        this.postList = postList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if(postList != null) {
            Post post = postList.get(position);
            if (post.getPostType().equals(PostTypes.IMAGE_POST)) {
                return PostViewTypes.IMAGE_POST;
            }
            if (post.getPostType().equals(PostTypes.VIDEO_POST)) {
                return PostViewTypes.VIDEO_POST;
            }
            if (post.getPostType().equals(PostTypes.OTHER_SOURCE_VIDEO_POST)) {
                return PostViewTypes.OTHER_SOURCE_VIDEO_POST;
            } else {
                return PostViewTypes.VIEW_TYPE_NOT_HAVE;
            }
        }
        else {
            return 0;
        }
    }

    @NonNull
    @Override
    public SelectablePostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == PostViewTypes.IMAGE_POST){
            itemView = inflater.inflate(R.layout.selectable_image_post_item,parent,false);
            return new SelectableImagePostHolder(itemView);
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectablePostHolder holder, int position) {
        if(holder instanceof SelectableImagePostHolder){
            ((SelectableImagePostHolder)holder).setData(postList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(postList == null){
            return 0;
        }
        else {
            return postList.size();
        }
    }

    abstract class SelectablePostHolder extends RecyclerView.ViewHolder {
        private boolean isSelected = false;
        private View itemView;
        public SelectablePostHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
        void setData(Post post, View selectionMarker){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectionMarker != null) {
                        if (isSelected == false) {
                            showDownAnimation.setTarget(selectionMarker);
                            selectionMarker.setVisibility(View.VISIBLE);
                            showDownAnimation.start();
                            selectedPostList.add(post);
                            isSelected = true;
                        } else {
                            hideUpAnimation.setTarget(selectionMarker);
                            hideUpAnimation.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    selectionMarker.setVisibility(View.INVISIBLE);
                                }
                            });
                            hideUpAnimation.start();
                            selectedPostList.remove(post);
                            isSelected = false;
                        }
                    }
                }
            });
        }
    }

    class SelectableImagePostHolder extends SelectablePostHolder {
        View selectionMarker;
        SimpleDraweeView selectableImagePostPreview;
        public SelectableImagePostHolder(@NonNull View itemView) {
            super(itemView);
            selectionMarker = itemView.findViewById(R.id.imagePostSelectionMarker);
            selectableImagePostPreview = itemView.findViewById(R.id.selectableImagePostPreview);
        }

        public void setData(Post post) {
            super.setData(post,selectionMarker);
            if(post.getPostType().equals(PostTypes.IMAGE_POST)){
                selectableImagePostPreview.setImageURI(((ImagePost)post).getPostImageDownloadUriList().get(0));
            }
        }
    }
    //TODO: Add other view types
}
