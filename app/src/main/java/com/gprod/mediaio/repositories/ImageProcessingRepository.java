package com.gprod.mediaio.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gprod.mediaio.services.imageProcessing.ImageProcessorService;

import java.util.ArrayList;

public class ImageProcessingRepository {
    private static ImageProcessingRepository instance;
    private ImageProcessorService imageProcessorService;
    private Bitmap outputImage;
    private Bitmap sourceImage;
    private ArrayList<Bitmap> renderStack = new ArrayList<>();
    private int renderStackIndex = 1;

    public static ImageProcessingRepository getInstance() {
        instance = new ImageProcessingRepository();
        return instance;
    }
    private ImageProcessingRepository(){
        imageProcessorService = ImageProcessorService.getInstance();
    }
    public void loadImage(Bitmap bitmap){
        renderStack.add(bitmap);
        sourceImage = bitmap;
    }
    public Bitmap renderBrightness(int brightness){
        outputImage = imageProcessorService.brightness(renderStack.get(renderStack.size() - renderStackIndex),brightness);
        return outputImage;
    }
    public Bitmap renderContrast(int contrast){
        outputImage = imageProcessorService.contrast(renderStack.get(renderStack.size() - renderStackIndex),(float)contrast/10);
        return outputImage;
    }
    public Bitmap renderSaturation(int saturation){
        outputImage = imageProcessorService.saturation(renderStack.get(renderStack.size() - renderStackIndex),saturation);
        return outputImage;
    }
    public Bitmap renderHue(int hue){
        outputImage = imageProcessorService.hue(renderStack.get(renderStack.size() - renderStackIndex),(float)hue/10);
        return outputImage;
    }
    public void acceptChanges(){
        renderStack.add(outputImage);
    }
    public Bitmap denyChanges(){
        return renderStack.get(renderStack.size() - renderStackIndex);
    }
    public Bitmap getActualImage(){
        return renderStack.get(renderStack.size() - renderStackIndex);
    }


}
