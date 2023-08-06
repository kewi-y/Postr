package com.gprod.mediaio.services.storage.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gprod.mediaio.interfaces.services.storage.GettingFileDownloadUriCallback;
import com.gprod.mediaio.interfaces.services.storage.UploadFileCallback;

import java.io.ByteArrayOutputStream;

public class FirebaseStorageService {
    private static FirebaseStorageService instance;
    private FirebaseStorage storage;
    private StorageReference reference;
    public static FirebaseStorageService getInstance(){
        if(instance == null){
            instance = new FirebaseStorageService();
        }
        return instance;
    }

    private FirebaseStorageService(){
        storage = FirebaseStorage.getInstance();
    }

    public void uploadImage(Bitmap image, String imageId, UploadFileCallback uploadFileCallback){
        reference = storage.getReference("images");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageBitmapList = byteArrayOutputStream.toByteArray();
        UploadTask imageUploadTask = reference.child(imageId).putBytes(imageBitmapList);
        imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.child(imageId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadFileCallback.onSuccess(uri);
                    }
                });

            }
        });
        imageUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadFileCallback.onFailure();
            }
        });
    }
    public void getImageDownloadUri(String imageId, GettingFileDownloadUriCallback gettingFileDownloadUriCallback){
        reference = storage.getReference("images");
        reference.child(imageId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                gettingFileDownloadUriCallback.onSuccess(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                gettingFileDownloadUriCallback.onFailure();
            }
        });
    }
    public void uploadVideo(byte[] videoBytes, String videoId, UploadFileCallback uploadFileCallback){
        reference = storage.getReference("videos");
        UploadTask uploadTask = reference.child(videoId).putBytes(videoBytes);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int percent = (int) ((float)snapshot.getBytesTransferred()/snapshot.getTotalByteCount() * 100);
                uploadFileCallback.onProgress(percent);
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.child(videoId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadFileCallback.onSuccess(uri);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadFileCallback.onFailure();
            }
        });
    }
}
