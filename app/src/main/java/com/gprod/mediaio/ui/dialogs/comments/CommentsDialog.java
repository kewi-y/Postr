package com.gprod.mediaio.ui.dialogs.comments;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.CommentListAdapter;
import com.gprod.mediaio.interfaces.dialogs.comments.AddCommentListener;
import com.gprod.mediaio.models.post.Comment;

import java.util.ArrayList;

public class CommentsDialog {
    private static CommentsDialog instance;
    private BottomSheetDialog dialog;
    private RecyclerView commentListView;
    private EditText editTextComment;
    private ImageButton sendCommentButton;
    private ImageButton closeCommentsDialogButton;
    private LayoutAnimationController commentItemAnimation;
    private ArrayList<Comment> commentList;
    private CommentListAdapter commentListAdapter;
    public static CommentsDialog getInstance(Context context, Activity activity) {
        if(instance == null){
            instance = new CommentsDialog(context, activity);
        }
        return instance;
    }
    private CommentsDialog(Context context, Activity activity ){
        dialog = new BottomSheetDialog(activity);
        dialog.setContentView(R.layout.comments_dialog);
        commentItemAnimation = AnimationUtils.loadLayoutAnimation(context,R.anim.comments_items_animation);
        commentListView = dialog.findViewById(R.id.dialogCommentListView);
        commentListView.setLayoutManager(new LinearLayoutManager(context));
        closeCommentsDialogButton = dialog.findViewById(R.id.closeCommentsDialogButton);
        editTextComment = dialog.findViewById(R.id.editTextComment);
        sendCommentButton = dialog.findViewById(R.id.sendCommentButton);
    }
    public void updateCommentList(Context context,ArrayList<Comment> commentList) {
        if(commentListAdapter == null){
            commentListAdapter = new CommentListAdapter(context,commentList);
            commentListView.setAdapter(commentListAdapter);
        }
        else {
            commentListAdapter.updateCommentList(commentList);
            commentListAdapter.notifyDataSetChanged();
        }
    }

    public void show(Context context, AddCommentListener addCommentListener){
        commentListView.setLayoutAnimation(commentItemAnimation);
        closeCommentsDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
        if(commentList != null){
            commentListView.setAdapter(new CommentListAdapter(context,commentList));
        }
        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = editTextComment.getText().toString();
                if(commentText != null && !commentText.isEmpty()){
                    addCommentListener.onAddComment(commentText);
                    editTextComment.getText().clear();
                }
            }
        });
        dialog.show();
    }
}
