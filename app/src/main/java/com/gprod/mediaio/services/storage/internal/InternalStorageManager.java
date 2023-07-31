package com.gprod.mediaio.services.storage.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InternalStorageManager {
    private static InternalStorageManager instance;
    public static InternalStorageManager getInstance (){
        if(instance == null){
            instance = new InternalStorageManager();
        }
        return instance;
    }
    private InternalStorageManager(){}

    public void deleteFile(File file){
        if(file.exists()){
            file.delete();
        }
    }

    public File saveBitmap(Context context,Bitmap image,String fileName){
        File saveFileDir = new File(context.getFilesDir(),fileName);
        try {
            FileOutputStream saveFileOutputStream = new FileOutputStream(saveFileDir);
            image.compress(Bitmap.CompressFormat.JPEG,100,saveFileOutputStream);
            return saveFileDir;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Uri getFileUri(File file){
        try {
            return Uri.parse(file.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFileByUri(Uri uri){
        File readFileDir = new File(uri.toString());
        if(readFileDir.exists()){
            return readFileDir;
        }
        else{
            return null;
        }
    }
}
