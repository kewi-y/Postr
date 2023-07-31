package com.gprod.mediaio.services.popup.loading;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.gprod.mediaio.R;

public class LoadingPopup {
    private static LoadingPopup instance;
    private ProgressBar progressBar;
    private Animator showAnimator;
    private Animator hideAnimator;
    public static void initialize(ProgressBar progressBar){
        if(instance == null){
            instance = new LoadingPopup(progressBar);
        }
    }
    private LoadingPopup(ProgressBar progressBar){
        this.progressBar = progressBar;
    }
    public static void show(Context context){
        Log.d("MY LOGS", "Show loading popup from context >>: " + context.toString());
        instance.progressBar.bringToFront();
        instance.showAnimator = AnimatorInflater.loadAnimator(context,R.animator.loading_show);
        instance.showAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                instance.progressBar.setVisibility(View.VISIBLE);
            }
        });
        instance.showAnimator.setTarget(instance.progressBar);
        instance.showAnimator.start();
    }
    public static void hide(Context context){
        Log.d("MY LOGS", "Hide loading popup from context >>: " + context.toString());
        instance.hideAnimator = AnimatorInflater.loadAnimator(context,R.animator.loading_hide);
        instance.hideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                instance.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        instance.hideAnimator.setTarget(instance.progressBar);
        instance.hideAnimator.start();
    }
}
