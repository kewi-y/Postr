package com.gprod.mediaio.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.gprod.mediaio.R;
import com.gprod.mediaio.services.storage.internal.InternalStorageManager;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.util.ArrayList;

public class TempPhotoRepository {
    private Bitmap tempImage;
    private File tempImageFile;
    private ArrayList<Bitmap> attachedImageList = new ArrayList<>();
    private static TempPhotoRepository instance;
    private InternalStorageManager internalStorageManager;
    public static TempPhotoRepository getInstance() {
        if(instance == null){
            instance = new TempPhotoRepository();
        }
        return instance;
    }
    private TempPhotoRepository(){
        internalStorageManager = InternalStorageManager.getInstance();
    }

    public void setTempImage(Context context, Bitmap tempImage){
        this.tempImage = tempImage;
        tempImageFile = internalStorageManager.saveBitmap(context,tempImage,context.getResources().getString(R.string.temp_photo_name));
    }

    public Bitmap getTempImageBitmap(){
        if(tempImage != null){
            return tempImage;
        }
        else {
            return null;
        }
    }
    public Uri getTempImageUri(){
        return internalStorageManager.getFileUri(tempImageFile);
    }
    public void clearTempImage(){
        if(tempImage != null){
            tempImage = null;
            internalStorageManager.deleteFile(tempImageFile);
        }
    }
    public void attachImage(Bitmap image){
        attachedImageList.add(image);
    }
    public void detachImage(Bitmap image){
        attachedImageList.remove(image);
    }
    public void clearAttachedImageList(){
        attachedImageList.clear();
    }
    public ArrayList<Bitmap> getAttachedImageList(){
        return attachedImageList;
    }
}
