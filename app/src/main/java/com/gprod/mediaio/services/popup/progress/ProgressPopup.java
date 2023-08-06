package com.gprod.mediaio.services.popup.progress;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.view.View;
import android.widget.ProgressBar;

import com.gprod.mediaio.R;

public class ProgressPopup {
    private static ProgressPopup instance;
    private ProgressBar progressBar;
    private View popupView;
    private boolean isShow = false;
    private Animator showAnimation;
    private Animator hideAnimation;
    public static void initialize(View popupView, ProgressBar progressBar) {
        if(instance == null){
            instance = new ProgressPopup(popupView,progressBar);
        }
    }
    private ProgressPopup(View popupView,ProgressBar progressBar){
        this.progressBar = progressBar;
        this.popupView = popupView;
    }
    public static void show(Context context){
        instance.popupView.bringToFront();
        instance.progressBar.setProgress(0);
        instance.showAnimation = AnimatorInflater.loadAnimator(context, R.animator.loading_show);
        instance.showAnimation.setTarget(instance.popupView);
        instance.showAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                instance.popupView.setVisibility(View.VISIBLE);
            }
        });
        instance.showAnimation.start();
        instance.isShow = true;
    }
    public static void hide(Context context){
        instance.popupView.bringToFront();
        instance.hideAnimation = AnimatorInflater.loadAnimator(context,R.animator.loading_hide);
        instance.hideAnimation.setTarget(instance.popupView);
        instance.hideAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                instance.popupView.setVisibility(View.INVISIBLE);
                instance.isShow = false;
            }
        });
        instance.hideAnimation.start();
    }
    public static void setProgress(int percent){
        if(instance.isShow){
            instance.progressBar.setProgress(percent);
        }
    }
    public static boolean isShow(){
        return instance.isShow;
    }
}
