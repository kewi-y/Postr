package com.gprod.mediaio.services.popup.notification;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.gprod.mediaio.R;

public class NotificationPopup {
    private static NotificationPopup instance;
    private TextView popupTextView;
    private Animator animator;
    public static void initialize(TextView popupTextView){
        if(instance == null){
            instance = new NotificationPopup(popupTextView);
        }
    }

    private NotificationPopup(TextView popupTextView){
        this.popupTextView = (TextView) popupTextView;
    }
    public static void show(Context context, boolean isError, String text){
        instance.popupTextView.bringToFront();
        instance.animator = AnimatorInflater.loadAnimator(context,R.animator.notification_show);
        if(isError){
            instance.popupTextView.setBackgroundResource(R.drawable.notification_error_background);
        }
        else{
            instance.popupTextView.setBackgroundResource(R.drawable.notification_info_background);
        }
        instance.animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                instance.popupTextView.setVisibility(View.INVISIBLE);
            }
        });
        instance.popupTextView.setText(text);
        instance.animator.setTarget(instance.popupTextView);
        instance.popupTextView.setVisibility(View.VISIBLE);
        instance.animator.start();
    }
}
