package com.gprod.mediaio.ui.fragments.add.story;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.tabs.TabLayout;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.camera.CameraType;
import com.gprod.mediaio.interfaces.fragments.add.story.CapturingImageStoryCallback;
import com.gprod.mediaio.interfaces.fragments.add.story.RecordingVideoStoryCallback;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;
import com.gprod.mediaio.services.popup.progress.ProgressPopup;

import java.util.Map;

public class AddStoryFragment extends Fragment {

    private AddStoryViewModel viewModel;
    private ImageView startRecordImageView;
    private PreviewView cameraPreview;
    private TabLayout cameraModeSelector;
    private CameraType cameraType;
    private NavController navController;
    private ImageView flipCameraView;
    private ProgressBar recordingStoryProgressBar;
    private boolean isRecording = false;
    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AddStoryViewModel.class);
        viewModel.init(getContext(),getResources().getString(R.string.story_api_url));
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        View root = inflater.inflate(R.layout.add_story_fragment, container, false);
        startRecordImageView = root.findViewById(R.id.startRecordImageView);
        cameraPreview = root.findViewById(R.id.addStoryCameraPreview);
        cameraModeSelector = root.findViewById(R.id.cameraModeSelectorTabLayout);
        recordingStoryProgressBar = root.findViewById(R.id.recordingStoryProgressBar);
        flipCameraView = root.findViewById(R.id.flipCameraImageView);
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                navController.popBackStack();
                this.remove();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(requestMultiplePermissionsContract,
                new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if(result.get(PERMISSIONS[0]) != null && result.get(PERMISSIONS[1]) != null
                && result.get(PERMISSIONS[2]) != null && result.get(PERMISSIONS[3]) != null){
                    if(result.get(PERMISSIONS[0]) && result.get(PERMISSIONS[1])
                    && result.get(PERMISSIONS[2]) && result.get(PERMISSIONS[3])){
                        viewModel.startCamera(getContext(),cameraPreview,getViewLifecycleOwner());
                    }
                    else {
                        NotificationPopup.show(getContext(),true,getResources().getString(R.string.error_permissions_not_granted_title));
                    }
                }
            }
        });
        if(viewModel.checkPermission(getContext())){
            viewModel.startCamera(getContext(),cameraPreview,getViewLifecycleOwner());
        }
        else {
            requestPermissionLauncher.launch(PERMISSIONS);
        }
        cameraModeSelector.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        cameraType = CameraType.IMAGE_TYPE;
                        break;
                    case 1:
                        cameraType = CameraType.VIDEO_TYPE;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        startRecordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraType != null){
                    if(cameraType == CameraType.VIDEO_TYPE){
                        if(!isRecording){
                            AnimatedVectorDrawable avdStartRecord = (AnimatedVectorDrawable) startRecordImageView.getDrawable();
                            AnimatedVectorDrawable avdStopRecord = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_stop_record_anim,getActivity().getTheme());
                            viewModel.recordVideoStory(getContext(), new RecordingVideoStoryCallback() {
                                @Override
                                public void onStarted() {
                                    Log.d("MY LOGS","Start recording video story");
                                    recordingStoryProgressBar.setVisibility(View.VISIBLE);
                                    isRecording = true;
                                    avdStartRecord.start();
                                }

                                @Override
                                public void onProgress(int percent) {
                                    Log.d("MY LOGS", "Progress of recording story >>: " + percent + "%");
                                    recordingStoryProgressBar.setProgress(percent);
                                    if(percent == 100){
                                        recordingStoryProgressBar.setVisibility(View.INVISIBLE);
                                        avdStopRecord.start();
                                    }
                                }


                                @Override
                                public void onFinish() {
                                    Log.d("MY LOGS","On finish recording story");
                                    recordingStoryProgressBar.setVisibility(View.INVISIBLE);
                                    ProgressPopup.hide(getContext());
                                    navController.navigate(R.id.navigation_home);
                                    getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                                }
                                @Override
                                public void onUploadFileProgress(int percent) {
                                    startRecordImageView.setImageDrawable(avdStopRecord);
                                    avdStopRecord.start();
                                    Log.d("MY LOGS","load file progress >>: " + percent + "%");
                                    if(!ProgressPopup.isShow()){
                                        ProgressPopup.show(getContext());
                                    }
                                    ProgressPopup.setProgress(percent);
                                }

                                @Override
                                public void onFailure() {
                                    Log.d("MY LOGS","failed to record story");
                                    ProgressPopup.hide(getContext());
                                    navController.navigate(R.id.navigation_home);
                                    getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                        else {
                            viewModel.stopRecordVideoStory();
                            isRecording = false;
                        }
                    }
                    if(cameraType == CameraType.IMAGE_TYPE){
                        viewModel.captureImageStory(getContext(), new CapturingImageStoryCallback() {
                            @Override
                            public void onCaptured() {
                                ProgressPopup.hide(getContext());
                                navController.navigate(R.id.navigation_home);
                                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onUploadFileProgress(int percent) {
                                if(!ProgressPopup.isShow()){
                                    ProgressPopup.show(getContext());
                                }
                                ProgressPopup.setProgress(percent);
                            }

                            @Override
                            public void onFailure() {
                                //TODO: show error
                                ProgressPopup.hide(getContext());
                                navController.navigate(R.id.navigation_home);
                                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        });
        flipCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.flipCamera(getViewLifecycleOwner());
            }
        });
        return root;
    }
}