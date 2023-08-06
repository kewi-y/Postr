package com.gprod.mediaio.ui.fragments.add.story;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.fragments.add.story.CapturingImageStoryCallback;
import com.gprod.mediaio.interfaces.fragments.add.story.RecordingVideoStoryCallback;
import com.gprod.mediaio.interfaces.repositories.story.UploadFileProgressCallback;
import com.gprod.mediaio.interfaces.services.camera.CaptureVideoCallback;
import com.gprod.mediaio.interfaces.services.camera.TakingPhotoCallback;
import com.gprod.mediaio.interfaces.services.story.AddingStoryCallback;
import com.gprod.mediaio.repositories.StoryRepository;
import com.gprod.mediaio.repositories.UserRepository;
import com.gprod.mediaio.services.camera.CameraService;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;
import com.gprod.mediaio.services.storage.firebase.FirebaseStorageService;

public class AddStoryViewModel extends ViewModel {
    private StoryRepository storyRepository;
    private UserRepository userRepository;
    private CameraService cameraService;
    private CountDownTimer storyTimer;
    public AddStoryViewModel(){
        userRepository = UserRepository.getInstance();
    }
    public void init(Context context, String apiUrl){
        storyRepository = StoryRepository.getInstance(apiUrl);
        cameraService = CameraService.getInstance(context);
    }
    public boolean checkPermission(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            NotificationPopup.show(context,true,context.getResources().getString(R.string.error_permissions_not_granted_title));
            return false;
        }
    }
    public void startCamera(Context context, PreviewView previewView, LifecycleOwner owner){
        cameraService.startCamera(context,previewView,owner);
    }
    public void recordVideoStory(Context context, RecordingVideoStoryCallback recordingVideoStoryCallback){
        long storyLength = Long.parseLong(context.getResources().getString(R.string.story_length));
        long storyTimerTick = Long.parseLong(context.getResources().getString(R.string.story_timer_tick));
        storyTimer = new CountDownTimer(storyLength,storyTimerTick) {
            @Override
            public void onTick(long l) {
                int percent = (int) ((((float)storyLength - l) / ((float)storyLength)) * 100f);
                recordingVideoStoryCallback.onProgress(percent);
            }

            @Override
            public void onFinish() {
                Log.d("MY LOGS","Count down timer is end");
                cameraService.stopRecording();
            }
        };
        cameraService.startRecordingVideo(context, new CaptureVideoCallback() {
            @Override
            public void onCaptured(byte[] file) {
                storyRepository.addVideoStory(file, userRepository.getUser().getId(), new UploadFileProgressCallback() {
                    @Override
                    public void onProgress(int percent) {
                        recordingVideoStoryCallback.onUploadFileProgress(percent);
                    }
                }, new AddingStoryCallback() {
                    @Override
                    public void onSuccess() {
                        recordingVideoStoryCallback.onFinish();
                    }

                    @Override
                    public void onFailure() {
                        recordingVideoStoryCallback.onFailure();
                    }
                });
            }
            @Override
            public void onFailure() {
                recordingVideoStoryCallback.onFailure();
            }
        });
        storyTimer.start();
        recordingVideoStoryCallback.onStarted();
    }
    public void stopRecordVideoStory(){
        if(storyTimer != null) {
            cameraService.stopRecording();
            storyTimer.cancel();
        }
    }
    public void captureImageStory(Context context, CapturingImageStoryCallback capturingImageStoryCallback){
        cameraService.takePhoto(context, new TakingPhotoCallback() {
            @Override
            public void onTaken(Bitmap image) {
                storyRepository.addImageStory(image, userRepository.getUser().getId(), new UploadFileProgressCallback() {
                    @Override
                    public void onProgress(int percent) {
                        capturingImageStoryCallback.onUploadFileProgress(percent);
                    }
                }, new AddingStoryCallback() {
                    @Override
                    public void onSuccess() {
                        capturingImageStoryCallback.onCaptured();
                    }

                    @Override
                    public void onFailure() {
                        capturingImageStoryCallback.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                capturingImageStoryCallback.onFailure();
            }
        });
    }
}