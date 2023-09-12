package com.gprod.mediaio.ui.fragments.camera.image;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.camera.TakingPhotoCallback;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

import java.util.Map;

public class ImageCameraFragment extends Fragment {

    private ImageCameraViewModel viewModel;
    private ImageButton takePhotoButton;
    private PreviewView previewView;
    private ImageView flipCameraView;
    private NavController navController;
    private final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.image_camera_fragment, container, false);
        viewModel = new ViewModelProvider(this).get(ImageCameraViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        takePhotoButton = root.findViewById(R.id.takePhotoButton);
        previewView = root.findViewById(R.id.imageCameraPreviewView);
        flipCameraView = root.findViewById(R.id.flipCameraImageView);
        ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(requestMultiplePermissionsContract,
                new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if(result.get(PERMISSIONS[0]) != null && result.get(PERMISSIONS[1]) != null
                && result.get(PERMISSIONS[2]) != null && result.get(PERMISSIONS[3]) != null){
                    if(result.get(PERMISSIONS[0]) && result.get(PERMISSIONS[1])
                    && result.get(PERMISSIONS[2]) && result.get(PERMISSIONS[3])){
                        startCamera();
                    }
                    else {
                        NotificationPopup.show(getContext(),true,getResources().getString(R.string.error_permissions_not_granted_title));
                        navController.popBackStack();
                    }
                }
            }
        });
        if(viewModel.checkPermissions(getContext())) {
            startCamera();
        }
        else {
            requestPermissionsLauncher.launch(PERMISSIONS);
        }
        flipCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.flipCamera(getViewLifecycleOwner());
            }
        });
        return root;
    }
    private void startCamera(){
        viewModel.initCamera(getContext(),getViewLifecycleOwner(),previewView);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.takePhoto(getContext(), new TakingPhotoCallback() {
                    @Override
                    public void onTaken(Bitmap image) {
                        navController.navigate(R.id.action_image_camera_fragment_to_imageProcessingFragment);
                    }
                    @Override
                    public void onFailure() {
                        NotificationPopup.show(getContext(), true, getResources().getString(R.string.error_taking_photo_title));
                    }
                });
            }
        });
    }
}