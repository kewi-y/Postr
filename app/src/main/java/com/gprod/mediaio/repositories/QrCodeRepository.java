package com.gprod.mediaio.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.Matrix;
import android.util.Size;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.gprod.mediaio.R;
import com.gprod.mediaio.services.storage.internal.InternalStorageManager;

import java.io.File;
import java.util.Hashtable;

public class QrCodeRepository {
    private static QrCodeRepository instance;
    private QRCodeWriter qrCodeWriter;
    private Size imageSize;
    private BitMatrix bitMatrix;
    private Bitmap qrCodeBitmap;
    private int primaryColor, backgroundColor;
    private InternalStorageManager internalStorageManager;
    private File tempQrFile;

    Hashtable<EncodeHintType, ErrorCorrectionLevel> errorCorrectionHint = new Hashtable<>();
    public static QrCodeRepository getInstance() {
        return instance;
    }
    public static void initialize(int primaryColor,int backgroundColor){
        instance = new QrCodeRepository(primaryColor,backgroundColor);
    }
    private QrCodeRepository(int primaryColor,int backgroundColor){
        this.backgroundColor = backgroundColor;
        this.primaryColor = primaryColor;
        qrCodeWriter = new QRCodeWriter();
        imageSize = new Size(600,600);
        qrCodeBitmap = Bitmap.createBitmap(imageSize.getWidth(),imageSize.getHeight(), Bitmap.Config.ARGB_8888);
        errorCorrectionHint.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.M);
        internalStorageManager = InternalStorageManager.getInstance();
    }
    public Bitmap generateFromString(String data) throws WriterException {
        qrCodeBitmap.eraseColor(backgroundColor);
        bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, imageSize.getWidth(), imageSize.getHeight());
        for(int i = 0; i < bitMatrix.getHeight(); i++){
            for(int j = 0; j < bitMatrix.getWidth(); j++){
                if(bitMatrix.get(j,i)){
                    qrCodeBitmap.setPixel(j,i,primaryColor);
                }
            }
        }
        return qrCodeBitmap;
    }
}
