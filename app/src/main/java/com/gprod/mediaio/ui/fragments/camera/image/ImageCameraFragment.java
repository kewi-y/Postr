package com.gprod.mediaio.ui.fragments.camera.image;

import androidx.camera.view.PreviewView;
import androidx.lifecycle.ViewModelProvider;

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

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.camera.TakingPhotoCallback;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

public class ImageCameraFragment extends Fragment {

    private ImageCameraViewModel viewModel;
    private ImageButton takePhotoButton;
    private PreviewView previewView;
    private NavController navController;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.image_camera_fragment, container, false);
        viewModel = new ViewModelProvider(this).get(ImageCameraViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        takePhotoButton = root.findViewById(R.id.takePhotoButton);
        previewView = root.findViewById(R.id.imageCameraPreviewView);
        if(viewModel.requestPermissions(getContext())) {
            viewModel.initCamera(getContext(), getViewLifecycleOwner(), previewView);
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
        else {
            navController.navigate(R.id.navigation_home);
        }
        return root;
    }
}