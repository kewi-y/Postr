package com.gprod.mediaio.services.imageProcessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.icu.util.RangeValueIterator;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;


public class ImageProcessorService {
    private static ImageProcessorService instance;
    private Bitmap outputImage;
    private Paint paintFilter;
    private Canvas outputBitmapCanvas;
    public static ImageProcessorService getInstance() {
        if (instance == null) {
            instance = new ImageProcessorService();
        }
        return instance;
    }
    private ImageProcessorService(){
        paintFilter = new Paint();
    }
    public Bitmap brightness(Bitmap image,float value){
        outputImage = image.copy(Bitmap.Config.ARGB_8888,true);
        ColorMatrix brightnessColorMatrix = new ColorMatrix();
        brightnessColorMatrix.set(new float[]{
                1,0,0,0,value,
                0,1,0,0,value,
                0,0,1,0,value,
                0,0,0,1,0});
        ColorMatrixColorFilter brightnessColorFilter = new ColorMatrixColorFilter(brightnessColorMatrix);
        outputBitmapCanvas = new Canvas(outputImage);
        paintFilter.setColorFilter(brightnessColorFilter);
        outputBitmapCanvas.drawBitmap(outputImage,0,0,paintFilter);
        return outputImage;
    }
    public Bitmap contrast(Bitmap image,float value){
        float scale = value + 1.0f;
        float translate = (-0.5f * scale + 0.5f) * 255.0f;
        outputImage = image.copy(Bitmap.Config.ARGB_8888,true);
        ColorMatrix contrastColorMatrix = new ColorMatrix();
        contrastColorMatrix.set(new float[]{
                scale,0,0,0,translate,
                0,scale,0,0,translate,
                0,0,scale,0,translate,
                0,0,0,1,0
        });
        ColorMatrixColorFilter contrastColorFilter = new ColorMatrixColorFilter(contrastColorMatrix);
        outputBitmapCanvas = new Canvas(outputImage);
        paintFilter.setColorFilter(contrastColorFilter);
        outputBitmapCanvas.drawBitmap(outputImage,0,0,paintFilter);
        return outputImage;
    }
    public Bitmap saturation(Bitmap image,float value){
        outputImage = image.copy(Bitmap.Config.ARGB_8888,true);
        ColorMatrix saturationColorMatrix = new ColorMatrix();
        saturationColorMatrix.setSaturation(value);
        ColorMatrixColorFilter saturationColorFilter = new ColorMatrixColorFilter(saturationColorMatrix);
        outputBitmapCanvas = new Canvas(outputImage);
        paintFilter.setColorFilter(saturationColorFilter);
        outputBitmapCanvas.drawBitmap(outputImage,0,0,paintFilter);
        return outputImage;
    }

    public Bitmap hue(Bitmap image,float value){
        outputImage = image.copy(Bitmap.Config.ARGB_8888,true);
        ColorMatrix hueColorMatrix = new ColorMatrix(new float[] {
                1,0,0,value,0,
                0,1,0,value/2,0,
                0,0,1,value/4,0,
                0,0,0,1,0
        });
        outputBitmapCanvas = new Canvas(outputImage);
        paintFilter.setColorFilter(new ColorMatrixColorFilter(hueColorMatrix));
        outputBitmapCanvas.drawBitmap(outputImage,0,0,paintFilter);
        return outputImage;
    }
}
