package com.gprod.mediaio.ui.dialogs.story;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.StoryViewAdapter;
import com.gprod.mediaio.interfaces.adapters.StoryAdapterCallbacks;
import com.gprod.mediaio.models.story.Story;
import com.gprod.mediaio.models.story.StoryItem;
import com.gprod.mediaio.ui.views.RoundedCornersLayout;

import java.util.ArrayList;

public class StoryViewDialog{
    Dialog dialog;
    private static StoryViewDialog instance;
    private ViewPager2 storiesPager;
    private TextView authorAccountnameView;
    private SimpleDraweeView authorProfilePhotoView;
    StoryItem storyItem;
    private DisplayMetrics metrics;
    private StoryViewAdapter storyViewAdapter;
    private View changePrevStory,changeNextStory;
    private int currentStoryIndex = 0;
    private StoryAdapterCallbacks storyAdapterCallbacks;
    public static StoryViewDialog getInstance(Context context) {
        if(instance == null){
            instance = new StoryViewDialog(context);
        }
        return instance;
    }
    private StoryViewDialog(Context context){
        metrics = new DisplayMetrics();
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.story_view_dialog);
        storiesPager = dialog.findViewById(R.id.storyViewPager);
        authorAccountnameView = dialog.findViewById(R.id.authorAccountnameView);
        authorProfilePhotoView = dialog.findViewById(R.id.authorProfilePhotoView);
        changeNextStory = dialog.findViewById(R.id.changeNextStoryView);
        changePrevStory = dialog.findViewById(R.id.changePrevStoryView);
        storyViewAdapter = new StoryViewAdapter(context);
        dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        storyViewAdapter.setDisplayMetricsForVideoStory(metrics);
        storiesPager.setUserInputEnabled(false);
    }
    public void setStories(StoryItem storyItem){
        this.storyItem = storyItem;
    }
    public void show(){
        if(storyItem != null && storyItem.getStories() != null){
            storyAdapterCallbacks = new StoryAdapterCallbacks() {
                @Override
                public void onStartStory() {
                    //TODO:show some progressbar
                }

                @Override
                public void onEndStory() {
                    currentStoryIndex++;
                    Log.d("MY LOGS", "current Story Index >>: " + currentStoryIndex);
                    if(currentStoryIndex < storyItem.getStories().size()){
                        storiesPager.setCurrentItem(currentStoryIndex);
                    }
                    else {
                        dialog.dismiss();
                    }
                }
            };
            changeNextStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentStoryIndex++;
                    if(currentStoryIndex < storyItem.getStories().size()){
                        storiesPager.setCurrentItem(currentStoryIndex);
                    }
                    else {
                        dialog.dismiss();
                    }
                }
            });
            changePrevStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentStoryIndex--;
                    if(currentStoryIndex < 0){
                        currentStoryIndex = 0;
                        storiesPager.setCurrentItem(currentStoryIndex);
                    }
                    else {
                        storiesPager.setCurrentItem(currentStoryIndex);
                    }
                }
            });
            storyViewAdapter.setStories(storyItem.getStories());
            storyViewAdapter.setStoryAdapterCallbacks(storyAdapterCallbacks);
            storiesPager.setAdapter(storyViewAdapter);
            storiesPager.setCurrentItem(currentStoryIndex);
            authorProfilePhotoView.setImageURI(storyItem.getUserProfilePhotoDownloadUri());
            authorAccountnameView.setText(storyItem.getAccountname());
            dialog.show();
        }
    }
}
