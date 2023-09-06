package com.gprod.mediaio.services.popup.nfc;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import com.gprod.mediaio.R;

public class NfcSharingPopup {
    private static NfcSharingPopup instance;
    private View popupView;
    private boolean isShow = false;
    private Animator showAnimation;
    private Animator hideAnimation;
    public static void initialize(View popupView) {
        if(instance == null){
            instance = new NfcSharingPopup(popupView);
        }
    }
    private NfcSharingPopup(View popupView){
        this.popupView = popupView;
    }
    public static void show(Context context){
        instance.popupView.bringToFront();
        instance.showAnimation = AnimatorInflater.loadAnimator(context, R.animator.loading_show);
        instance.showAnimation.setTarget(instance.popupView);
        instance.popupView.setVisibility(View.VISIBLE);
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
    public static boolean isShow(){
        return instance.isShow;
    }
}
