package com.gprod.mediaio.ui.fragments.qr.code;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gprod.mediaio.R;

public class QrCodeFragment extends Fragment {

    private QrCodeViewModel viewModel;
    private ImageView qrCodeView;
    private LiveData<Bitmap> qrCodeLiveData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(QrCodeViewModel.class);
        View root = inflater.inflate(R.layout.qr_code_fragment, container, false);
        qrCodeView = root.findViewById(R.id.qrCodeImageView);
        qrCodeLiveData = viewModel.getQrCodeLiveData();
        qrCodeLiveData.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                qrCodeView.setImageBitmap(bitmap);
            }
        });
        viewModel.generateQrCode(getContext());
        return root;
    }

}