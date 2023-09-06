package com.gprod.mediaio.ui.fragments.qr.scan;

import androidx.camera.view.PreviewView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.repositories.selectedUser.SelectUserCallback;
import com.gprod.mediaio.interfaces.services.camera.QrScanningCallback;
import com.gprod.mediaio.services.camera.CameraService;

public class ScanQrFragment extends Fragment {

    private ScanQrViewModel viewModel;
    private PreviewView previewView;
    private CameraService cameraService;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ScanQrViewModel.class);
        View root = inflater.inflate(R.layout.scan_qr_fragment, container, false);
        previewView = root.findViewById(R.id.scanQrCameraPreview);
        cameraService = CameraService.getInstance(getContext());
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        QrScanningCallback qrScanningCallback = new QrScanningCallback() {
            @Override
            public void onScanned(String content) {
                viewModel.selectUser(content, new SelectUserCallback() {
                    @Override
                    public void onSelected() {
                        navController.popBackStack();
                        navController.navigate(R.id.navigation_profile);
                    }

                    @Override
                    public void onFailure() {
                        //TODO: notify
                    }
                });
            }

            @Override
            public void onFailure() {
                //TODO: notify
            }
        };
        cameraService.startCameraWithQrAnalyzer(getContext(),previewView,getViewLifecycleOwner(),qrScanningCallback);
        return root;
    }
}