package com.gprod.mediaio.ui.fragments.qr.code;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.zxing.WriterException;
import com.gprod.mediaio.R;
import com.gprod.mediaio.repositories.QrCodeRepository;
import com.gprod.mediaio.repositories.UserRepository;

public class QrCodeViewModel extends ViewModel {
    private QrCodeRepository qrCodeRepository;
    private MutableLiveData<Bitmap> qrCodeLiveData = new MutableLiveData<>();
    public QrCodeViewModel(){
        qrCodeRepository = QrCodeRepository.getInstance();
    }

    public LiveData<Bitmap> getQrCodeLiveData() {
        return qrCodeLiveData;
    }

    public void loadQr(){
        if(qrCodeRepository.getTempQr() != null){
            qrCodeLiveData.setValue(qrCodeRepository.getTempQr());
            qrCodeRepository.deleteTempQr();
        }
    }
}