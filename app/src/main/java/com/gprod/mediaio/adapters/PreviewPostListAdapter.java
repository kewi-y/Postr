package com.gprod.mediaio.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.interfaces.adapters.DeletePostListener;
import com.gprod.mediaio.interfaces.adapters.PostClickListener;
import com.gprod.mediaio.interfaces.adapters.PostViewTypes;
import com.gprod.mediaio.interfaces.adapters.DragPostListener;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.post.Post;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PreviewPostListAdapter extends RecyclerView.Adapter<PreviewPostListAdapter.ProfilePostHolder> {
    private ArrayList<Post> postList;
    private LayoutInflater inflater;
    private PostClickListener postCLickListener;
    private DeletePostListener deletePostListener;
    private DragPostListener dragPostListener;
    private View deletingPostFieldView;
    private Vibrator vibrator;
    private AtomicInteger atomicInteger;
    private ClipData draggedItemClipData;

    public PreviewPostListAdapter(Context context, ArrayList<Post> postList, View deletingPostFieldView , PostClickListener postCLickListener){
        inflater = LayoutInflater.from(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.postList = postList;
        this.postCLickListener = postCLickListener;
        this.deletingPostFieldView = deletingPostFieldView;
        atomicInteger = new AtomicInteger();
    }
    public void setDeletePostClickListener(DeletePostListener deletePostListener){
        this.deletePostListener = deletePostListener;
    }

    public void setStartDragPostListener(DragPostListener dragPostListener) {
        this.dragPostListener = dragPostListener;
    }

    public void setPostList(ArrayList<Post> postList){
        this.postList = postList;
    }
    @Override
    public int getItemViewType(int position) {
        Post post = postList.get(position);
        if(post.getPostType().equals(PostTypes.IMAGE_POST)){
            return PostViewTypes.IMAGE_POST;
        }
        else if(post.getPostType().equals(PostTypes.VIDEO_POST)){
            return PostViewTypes.VIDEO_POST;
        }
        else if(post.getPostType().equals(PostTypes.OTHER_SOURCE_VIDEO_POST)){
            return PostViewTypes.OTHER_SOURCE_VIDEO_POST;
        }
        else{
            return PostViewTypes.VIEW_TYPE_NOT_HAVE;
        }
    }

    @NonNull
    @Override
    public ProfilePostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == PostViewTypes.IMAGE_POST){
            view = inflater.inflate(R.layout.image_post_profile_item,parent,false);
            return new ImageProfilePostHolder(view);
        }
        if(viewType == PostViewTypes.VIDEO_POST){
            //TODO: add layout for other posts type
        }
        if(viewType == PostViewTypes.OTHER_SOURCE_VIDEO_POST){

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostHolder holder, int position) {
        holder.setData(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    abstract class ProfilePostHolder extends RecyclerView.ViewHolder{
        View postView;
        public ProfilePostHolder(@NonNull View itemView) {
            super(itemView);
            postView = itemView;
            postView.setId(atomicInteger.incrementAndGet());
        }
        public void setData(Post post){
            postView.setVisibility(View.VISIBLE);
            postView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postCLickListener.onClick(post);
                }
            });
            postView.setLongClickable(true);
            if(deletePostListener != null && dragPostListener != null) {
                postView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipData.Item postViewItem = new ClipData.Item(String.valueOf(postView.getId()));
                        draggedItemClipData = new ClipData(String.valueOf(postView.getId()),
                                new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                                postViewItem);
                        postView.startDragAndDrop(draggedItemClipData,new View.DragShadowBuilder(postView),null,0);
                        return true;
                    }
                });
                postView.setOnDragListener(new View.OnDragListener() {
                    ClipDescription draggedItemClipDescription;
                    @Override
                    public boolean onDrag(View view, DragEvent dragEvent) {
                        if(dragPostListener != null) {
                            if (dragEvent.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                                if (vibrator != null) {
                                    dragPostListener.onDrag();
                                    vibrator.vibrate(70);
                                    if(dragEvent.getClipDescription() != null &&
                                            dragEvent.getClipDescription().getLabel().equals(String.valueOf(postView.getId()))){
                                        draggedItemClipDescription = dragEvent.getClipDescription();
                                        postView.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                            if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED){
                                if(dragEvent.getX() > deletingPostFieldView.getX() &&
                                        dragEvent.getY() < deletingPostFieldView.getY() + deletingPostFieldView.getHeight()){
                                    if(draggedItemClipDescription != null &&
                                            draggedItemClipDescription.getLabel().equals(String.valueOf(postView.getId()))){
                                        deletePostListener.onDelete(post);
                                        dragPostListener.onEnd();
                                    }
                                }
                                else {
                                    dragPostListener.onEnd();
                                    postView.setVisibility(View.VISIBLE);
                                }
                                vibrator.vibrate(70);
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    class ImageProfilePostHolder extends ProfilePostHolder {
        SimpleDraweeView profileImagePostPreview;
        public ImageProfilePostHolder(@NonNull View itemView) {
            super(itemView);
            profileImagePostPreview = itemView.findViewById(R.id.profileImagePostPreview);
        }
        @Override
        public void setData(Post post) {
            super.setData(post);
            if(post instanceof ImagePost){
                if(((ImagePost) post).getPostImageDownloadUriList().size() > 0 && ((ImagePost) post).getPostImageDownloadUriList() != null) {
                    profileImagePostPreview.setImageURI(((ImagePost) post).getPostImageDownloadUriList().get(0));
                }
            }
        }
    }
    class VideoProfilePostHolder extends ProfilePostHolder{

        public VideoProfilePostHolder(@NonNull View itemView) {
            super(itemView);
            //TODO: Find view for other post types
        }

        @Override
        public void setData(Post post) {
            super.setData(post);
            //TODO: Add data for other post types
        }
    }

    class OtherSourceVideoProfilePostHolder extends ProfilePostHolder {
        public OtherSourceVideoProfilePostHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void setData(Post post) {
            super.setData(post);
        }

    }

}
