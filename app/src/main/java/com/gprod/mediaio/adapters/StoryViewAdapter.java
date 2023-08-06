package com.gprod.mediaio.adapters;

import android.content.Context;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.story.StoryType;
import com.gprod.mediaio.interfaces.adapters.StoryAdapterCallbacks;
import com.gprod.mediaio.interfaces.adapters.StoryViewTypes;
import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.models.story.Story;
import com.gprod.mediaio.models.story.VideoStory;
import com.gprod.mediaio.ui.views.RoundedCornersLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StoryViewAdapter extends RecyclerView.Adapter<StoryViewAdapter.StoryViewItemHolder> {
    private LayoutInflater inflater;
    private ArrayList<Story> stories;
    private DisplayMetrics metrics;
    private StoryAdapterCallbacks storyAdapterCallbacks;
    public StoryViewAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }
    public void setDisplayMetricsForVideoStory(DisplayMetrics displayMetrics){
        this.metrics = displayMetrics;
    }
    private interface LoadingImageCallback{
        void onLoaded();
    }
    private LoadingImageCallback loadingImageCallback;
    public void setStories(ArrayList<Story> stories){
        this.stories = stories;
        notifyDataSetChanged();
    }
    public void setStoryAdapterCallbacks(StoryAdapterCallbacks storyAdapterCallbacks){
        this.storyAdapterCallbacks = storyAdapterCallbacks;
    }
    @Override
    public int getItemViewType(int position) {
        Story story = stories.get(position);
        if(story.getStoryType() == StoryType.IMAGE_STORY){
            return StoryViewTypes.IMAGE_STORY;
        }
        else if(story.getStoryType() == StoryType.VIDEO_STORY){
            return StoryViewTypes.VIDEO_STORY;
        }
        else {
            return StoryViewTypes.VIEW_TYPE_NOT_HAVE;
        }
    }

    @NonNull
    @Override
    public StoryViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == StoryViewTypes.IMAGE_STORY){
            view = inflater.inflate(R.layout.story_view_image_item,parent,false);
            StoryViewImageItemHolder storyViewImageItemHolder = new StoryViewImageItemHolder(view);
            return storyViewImageItemHolder;
        }
        else if(viewType == StoryViewTypes.VIDEO_STORY){
            view = inflater.inflate(R.layout.story_view_video_item,parent,false);
            StoryViewVideoItemHolder storyViewVideoItemHolder = new StoryViewVideoItemHolder(view);
            return storyViewVideoItemHolder;
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewItemHolder holder, int position) {
        if(holder instanceof StoryViewImageItemHolder){
            ((StoryViewImageItemHolder)holder).setData((ImageStory) stories.get(position));
        }
        else if(holder instanceof StoryViewVideoItemHolder){
            ((StoryViewVideoItemHolder)holder).setData((VideoStory) stories.get(position));
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull StoryViewItemHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder instanceof StoryViewImageItemHolder){
            ((StoryViewImageItemHolder) holder).stopTimer();
        }
    }

    @Override
    public int getItemCount() {
        if(stories == null){
            return 0;
        }
        else {
            return stories.size();
        }
    }

    class StoryViewItemHolder extends RecyclerView.ViewHolder{
        public StoryViewItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class StoryViewImageItemHolder extends StoryViewItemHolder{
        SimpleDraweeView storyImageView;
        CountDownTimer timer;
        private ProgressBar storyImageViewProgressBar;
        public StoryViewImageItemHolder(@NonNull View itemView) {
            super(itemView);
            storyImageView = itemView.findViewById(R.id.storyImageView);
            storyImageViewProgressBar = itemView.findViewById(R.id.storyImageViewProgressBar);
            long storyLength = Long.parseLong(itemView.getContext().getResources().getString(R.string.story_length));
            timer = new CountDownTimer(storyLength, 10) {
                @Override
                public void onTick(long l) {
                    float percent = (((float) storyLength - l) / (float) storyLength) * 100f;
                    storyImageViewProgressBar.setProgress((int) percent);
                }

                @Override
                public void onFinish() {
                    if(storyAdapterCallbacks != null){
                        storyAdapterCallbacks.onEndStory();
                        storyImageViewProgressBar.setProgress(0);
                    }
                }
            };
        }
        public void setData(ImageStory imageStory){
            loadingImageCallback = new LoadingImageCallback() {
                @Override
                public void onLoaded() {
                    timer.start();
                    storyAdapterCallbacks.onStartStory();
                }
            };
            LoadingImageControllerListener loadingImageControllerListener = new LoadingImageControllerListener(loadingImageCallback);
            ImageRequest imageRequest = ImageRequest.fromUri(imageStory.getDownloadImageUri());
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setControllerListener(loadingImageControllerListener)
                    .build();
            storyImageView.setController(draweeController);
        }
        public void stopTimer(){
            timer.cancel();
        }
    }

    class StoryViewVideoItemHolder extends StoryViewItemHolder{
        private VideoView storyVideoView;
        private ProgressBar loadingStoryProgressBar;
        public StoryViewVideoItemHolder(@NonNull View itemView) {
            super(itemView);
            storyVideoView = itemView.findViewById(R.id.storyVideoView);
            loadingStoryProgressBar = itemView.findViewById(R.id.loadingStoryProgressBar);
        }
        public void setData(VideoStory videoStory){
            MediaController mediaController = new MediaController(storyVideoView.getContext());
            storyVideoView.setVideoURI(Uri.parse(videoStory.getDownloadVideoUri()));
            storyVideoView.setMediaController(mediaController);
            storyVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                    if(i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                        loadingStoryProgressBar.setVisibility(View.INVISIBLE);
                        if(storyAdapterCallbacks != null){
                            storyAdapterCallbacks.onStartStory();
                        }
                        return true;
                    }
                    return false;
                }
            });
            storyVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            storyVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(storyAdapterCallbacks != null){
                        storyAdapterCallbacks.onEndStory();
                    }
                }
            });
            storyVideoView.start();
        }
    }

    private class LoadingImageControllerListener extends BaseControllerListener<ImageInfo>{
        private LoadingImageCallback loadingImageCallback;
        public LoadingImageControllerListener(LoadingImageCallback loadingImageCallback){
            this.loadingImageCallback = loadingImageCallback;
        }
        @Override
        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            loadingImageCallback.onLoaded();
        }
    }
}
