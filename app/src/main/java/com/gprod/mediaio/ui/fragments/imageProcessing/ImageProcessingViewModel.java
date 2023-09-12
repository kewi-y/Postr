package com.gprod.mediaio.ui.fragments.imageProcessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.repositories.ImageProcessingRepository;
import com.gprod.mediaio.repositories.TempPhotoRepository;

import java.util.ArrayList;

public class ImageProcessingViewModel extends ViewModel {
    private TempPhotoRepository tempPhotoRepository;
    private MutableLiveData<Bitmap> tempImageLiveData = new MutableLiveData<>();
    private ImageProcessingRepository imageProcessingRepository;
    public ImageProcessingViewModel(){
        tempPhotoRepository = TempPhotoRepository.getInstance();
        imageProcessingRepository = ImageProcessingRepository.getInstance();
        tempImageLiveData.setValue(tempPhotoRepository.getTempImageBitmap());
        imageProcessingRepository.loadImage(tempPhotoRepository.getTempImageBitmap());
    }
    public LiveData<Bitmap> getTempImageLiveData(){
        return tempImageLiveData;
    }
    
    public void saveChanges(Context context){
        if(tempImageLiveData.getValue() != null){
            tempPhotoRepository.setTempImage(context,tempImageLiveData.getValue());
        }
    }
    public void acceptChanges(){
        if(tempImageLiveData.getValue() != null){
            imageProcessingRepository.acceptChanges();
            tempImageLiveData.setValue(imageProcessingRepository.getActualImage());
        }
    }
    public void denyChanges(){
        imageProcessingRepository.denyChanges();
        tempImageLiveData.setValue(imageProcessingRepository.getActualImage());
    }
    public void renderBrightness(int value){
        Bitmap renderedImage = imageProcessingRepository.renderBrightness(value);
        tempImageLiveData.setValue(renderedImage);
        Log.d("MY LOGS", String.valueOf(tempImageLiveData.getValue()));
    }
    public void renderContrast(int value){
        Bitmap renderedImage = imageProcessingRepository.renderContrast(value);
        tempImageLiveData.setValue(renderedImage);
    }
    public void renderSaturation(int value){
        Bitmap renderedImage = imageProcessingRepository.renderSaturation(value);
        tempImageLiveData.setValue(renderedImage);
    }
    public void renderHue(int value){
        Bitmap renderedImage = imageProcessingRepository.renderHue(value);
        tempImageLiveData.setValue(renderedImage);
    }
    public void rotateRight(){
        Bitmap renderedImage = imageProcessingRepository.rotateRight();
        tempImageLiveData.setValue(renderedImage);
    }

}