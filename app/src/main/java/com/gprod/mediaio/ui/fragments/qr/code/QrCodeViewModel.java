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
    private UserRepository userRepository;
    private QrCodeRepository qrCodeRepository;
    private MutableLiveData<Bitmap> qrCodeLiveData = new MutableLiveData<>();
    public QrCodeViewModel(){
        qrCodeRepository = QrCodeRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    public LiveData<Bitmap> getQrCodeLiveData() {
        return qrCodeLiveData;
    }

    public void generateQrCode(Context context){
        String id = userRepository.getUser().getId();
        String qrSharingTag = context.getResources().getString(R.string.qr_sharing_tag);
        try {
            id = qrSharingTag + id;
            Bitmap qrCode = qrCodeRepository.generateFromString(id);
            qrCodeLiveData.setValue(qrCode);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}