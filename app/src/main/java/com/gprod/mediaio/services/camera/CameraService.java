package com.gprod.mediaio.services.camera;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaActionSound;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.view.PreviewView;
import androidx.camera.view.video.OutputFileOptions;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.camera.CaptureVideoCallback;
import com.gprod.mediaio.interfaces.services.camera.TakingPhotoCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

public class CameraService {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private static CameraService instance;
    private Preview preview;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    public static CameraService getInstance(Context context) {
        if(instance == null) {
            instance = new CameraService(context);
        }
        return instance;
    }
    @SuppressLint("RestrictedApi")
    private CameraService(Context context){
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build();
        videoCapture = new VideoCapture.Builder().setVideoFrameRate(60).build();
    }

    public void startCamera(Context context,PreviewView previewView,LifecycleOwner owner){
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
                    cameraProvider.bindToLifecycle(owner,cameraSelector,preview,imageCapture,videoCapture);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void takePhoto(Context context,TakingPhotoCallback takingPhotoCallback){
        File tempPhotoFile = new File(context.getFilesDir(),context.getResources().getString(R.string.temp_photo_name));
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(tempPhotoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                try {
                    Bitmap resultImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(),outputFileResults.getSavedUri());
                    takingPhotoCallback.onTaken(resultImage);
                    File resultImageFile = new File(outputFileResults.getSavedUri().toString());
                    if(resultImageFile.exists()){
                        resultImageFile.delete();
                    }
                } catch (IOException e) {
                    takingPhotoCallback.onFailure();
                }

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                takingPhotoCallback.onFailure();
            }
        });
    }
    public void captureVideo(Context context, CaptureVideoCallback captureVideoCallback){

    }

}
