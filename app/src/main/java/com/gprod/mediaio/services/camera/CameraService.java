package com.gprod.mediaio.services.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.camera.CaptureVideoCallback;
import com.gprod.mediaio.interfaces.services.camera.QrScanningCallback;
import com.gprod.mediaio.interfaces.services.camera.TakingPhotoCallback;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraService {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private static CameraService instance;
    private Preview preview;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private ImageAnalysis imageQrAnalysis;
    private ProcessCameraProvider cameraProvider;
    private BarcodeScannerOptions scannerOptions;
    private BarcodeScanner barcodeScanner;
    private int cameraSelectorData;
    public static CameraService getInstance(Context context) {
        if(instance == null) {
            instance = new CameraService(context);
        }
        return instance;
    }
    @SuppressLint("RestrictedApi")
    private CameraService(Context context){
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraSelectorData = CameraSelector.LENS_FACING_FRONT;
        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build();
        videoCapture = new VideoCapture.Builder().setVideoFrameRate(60).build();
        imageQrAnalysis = new ImageAnalysis.Builder().build();
        scannerOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();
        OrientationEventListener orientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }

                imageCapture.setTargetRotation(rotation);
            }
        };
        orientationEventListener.enable();
    }

    public void startCamera(Context context,PreviewView previewView,LifecycleOwner owner){
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraSelectorData).build();
                    cameraProvider.bindToLifecycle(owner,cameraSelector,preview,imageCapture,videoCapture);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(context));
    }
    public void startCameraWithQrAnalyzer(Context context, PreviewView previewView, LifecycleOwner owner, QrScanningCallback qrScanningCallback){
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        barcodeScanner = BarcodeScanning.getClient(scannerOptions);
        String qrSharingTag = context.getResources().getString(R.string.qr_profile_sharing_tag);

        imageQrAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), new ImageAnalysis.Analyzer() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                if(imageProxy.getImage() != null){
                    InputImage inputImage = InputImage.fromMediaImage(imageProxy.getImage(),imageProxy.getImageInfo().getRotationDegrees());
                    barcodeScanner.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if(barcodes.size() > 0){
                                Barcode barcode = barcodes.get(0);
                                String barcodeContent = new String(barcode.getRawBytes(),StandardCharsets.UTF_8);
                                if(barcodeContent.contains(qrSharingTag)){
                                    barcodeContent = barcodeContent.replaceAll(qrSharingTag,"");
                                    qrScanningCallback.onScanned(barcodeContent);
                                    barcodeScanner.close();
                                }

                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            imageProxy.close();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            qrScanningCallback.onFailure();
                        }
                    });
                }
            }
        });
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                    cameraProvider.bindToLifecycle(owner,cameraSelector,preview, imageQrAnalysis);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },ContextCompat.getMainExecutor(context));

    }

    public void takePhoto(Context context,TakingPhotoCallback takingPhotoCallback){
        File tempPhotoFile = new File(context.getFilesDir(),context.getResources().getString(R.string.temp_photo_name));
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(tempPhotoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                try {
                    if(tempPhotoFile.exists()){
                        Bitmap resultImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(tempPhotoFile));
                        takingPhotoCallback.onTaken(resultImage);
                        tempPhotoFile.delete();

                    }
                } catch (IOException e) {
                    takingPhotoCallback.onFailure();
                }

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                takingPhotoCallback.onFailure();
                exception.printStackTrace();
            }
        });
    }
    @SuppressLint({"RestrictedApi", "MissingPermission"})
    public void startRecordingVideo(Context context, CaptureVideoCallback captureVideoCallback){
        File tempVideoFile = new File(context.getFilesDir(),context.getString(R.string.temp_video_name));
        VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(tempVideoFile).build();
        videoCapture.startRecording(outputFileOptions, ContextCompat.getMainExecutor(context), new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                Log.d("MY LOGS","File saved >>: " + outputFileResults.getSavedUri().toString());
                if(tempVideoFile.exists()){
                    try {
                        byte[] tempBytesFile = Files.readAllBytes(Paths.get(tempVideoFile.getPath()));
                        captureVideoCallback.onCaptured(tempBytesFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Log.d("MY LOGS", "file does not exists (why ???)");
                    captureVideoCallback.onFailure();
                }
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                Log.d("MY LOGS",message);
                captureVideoCallback.onFailure();
            }
        });
    }
    @SuppressLint("RestrictedApi")
    public void stopRecording(){
        videoCapture.stopRecording();
    }
    public void flipCamera(LifecycleOwner lifecycleOwner){
        CameraSelector cameraSelector;
        if(cameraProvider != null && preview != null){
            if(cameraSelectorData == CameraSelector.LENS_FACING_FRONT){
                cameraSelectorData = CameraSelector.LENS_FACING_BACK;
            }
            else if(cameraSelectorData == CameraSelector.LENS_FACING_BACK){
                cameraSelectorData = CameraSelector.LENS_FACING_FRONT;
            }
            cameraProvider.unbindAll();
            cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraSelectorData).build();
            cameraProvider.bindToLifecycle(lifecycleOwner,cameraSelector,imageCapture,videoCapture,preview);
        }
    }
}
