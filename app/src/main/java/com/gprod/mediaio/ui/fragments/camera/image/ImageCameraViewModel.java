package com.gprod.mediaio.ui.fragments.camera.image;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.camera.TakingPhotoCallback;
import com.gprod.mediaio.repositories.TempPhotoRepository;
import com.gprod.mediaio.services.camera.CameraService;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

public class ImageCameraViewModel extends ViewModel {
    private TempPhotoRepository tempPhotoRepository;
    private CameraService cameraService;
    public ImageCameraViewModel(){
        tempPhotoRepository = TempPhotoRepository.getInstance();
    }

    public boolean checkPermissions(Context context){
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            return false;
        }
    }
    public void initCamera(Context context, LifecycleOwner owner, PreviewView previewView){
        cameraService = CameraService.getInstance(context);
        cameraService.startCamera(context,previewView,owner);
    }
    public void flipCamera(LifecycleOwner owner){
        cameraService.flipCamera(owner);
    }
    public void takePhoto(Context context, TakingPhotoCallback takingPhotoCallback) {
        cameraService.takePhoto(context, new TakingPhotoCallback() {
            @Override
            public void onTaken(Bitmap image) {
                tempPhotoRepository.setTempImage(context,image);
                takingPhotoCallback.onTaken(image);
            }

            @Override
            public void onFailure() {
                takingPhotoCallback.onFailure();
            }
        });
    }
}