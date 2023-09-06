package com.gprod.mediaio.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.models.post.Comment;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {

    private ArrayList<Comment> commentList;
    private LayoutInflater inflater;
    public CommentListAdapter(Context context, ArrayList<Comment> commentList){
        this.commentList = commentList;
        inflater = LayoutInflater.from(context);
    }
    public void updateCommentList(ArrayList<Comment> commentList){
        this.commentList = commentList;
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View commentView = inflater.inflate(R.layout.comment_item,parent,false);
        return new CommentViewHolder(commentView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment currentComment = commentList.get(position);
        holder.setData(currentComment.getContent(),currentComment.getAuthorName(),currentComment.getAuthorTag(),currentComment.getAuthorProfilePhotoDownloadUri());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView commentAuthorPhotoView;
        TextView commentContentTextView,commentAuthorNameView,commentAuthorTagView;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentAuthorPhotoView = itemView.findViewById(R.id.commentAuthorPhotoView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentAuthorNameView = itemView.findViewById(R.id.commentAuthorNameView);
            commentAuthorTagView = itemView.findViewById(R.id.commentAuthorTagView);
        }
        public void setData(String commentContent,String commentAuthorName,String commentAuthorTag,String commentAuthorDownloadPhotoUri){
            commentAuthorNameView.setText(commentAuthorName);
            commentContentTextView.setText(commentContent);
            commentAuthorPhotoView.setImageURI(Uri.parse(commentAuthorDownloadPhotoUri));
            commentAuthorTagView.setText(commentAuthorTag);
        }
    }
}
