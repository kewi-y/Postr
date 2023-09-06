package com.gprod.mediaio.ui.fragments.imageProcessing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.imageSettings.ImageSettingsTypes;

public class ImageProcessingFragment extends Fragment {

    private ImageProcessingViewModel viewModel;
    private ImageView imageProcessingPreview;
    private View imageSettingsBarLayout;
    private SeekBar imageSettingsBarView;
    private TextView imageSettingsTitleView;
    private BottomNavigationView imageSettingsBottomNavigationView;
    private LiveData<Bitmap> tempImageLiveData;
    private ImageSettingsTypes imageSettingsType;
    private ImageButton acceptChangesButton,denyChangesButton,saveImageButton;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ImageProcessingViewModel.class);
        View root = inflater.inflate(R.layout.image_processing_fragment, container, false);
        imageProcessingPreview = root.findViewById(R.id.imageProcessingPreview);
        imageSettingsBarLayout = root.findViewById(R.id.imageSettingsBarLayout);
        imageSettingsBarView = root.findViewById(R.id.imageSettingsBar);
        imageSettingsTitleView = root.findViewById(R.id.imageSettingsBarTitle);
        acceptChangesButton = root.findViewById(R.id.acceptChangesButton);
        saveImageButton = root.findViewById(R.id.saveImageButton);
        denyChangesButton = root.findViewById(R.id.denyChangesButton);
        imageSettingsBottomNavigationView = root.findViewById(R.id.imageSettingsBottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        tempImageLiveData = viewModel.getTempImageLiveData();
        imageSettingsBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.image_processing_brightness:
                        imageSettingsTitleView.setText(getResources().getString(R.string.title_image_brightness));
                        imageSettingsBarView.setMin(0);
                        imageSettingsBarView.setMax(100);
                        imageSettingsBarView.setProgress(0);
                        imageSettingsBarLayout.setVisibility(View.VISIBLE);
                        imageSettingsType = ImageSettingsTypes.BRIGHTNESS;
                        setEnableBottomMenu(false);
                        break;

                    case R.id.image_processing_contrast:
                        imageSettingsTitleView.setText(getResources().getString(R.string.title_image_contrast));
                        imageSettingsBarView.setMin(0);
                        imageSettingsBarView.setMax(20);
                        imageSettingsBarView.setProgress(10);
                        imageSettingsBarLayout.setVisibility(View.VISIBLE);
                        imageSettingsType = ImageSettingsTypes.CONTRAST;
                        setEnableBottomMenu(false);
                        break;

                    case R.id.image_processing_saturation:
                        imageSettingsTitleView.setText(getResources().getString(R.string.title_image_saturation));
                        imageSettingsBarView.setMin(0);
                        imageSettingsBarView.setMax(50);
                        imageSettingsBarView.setProgress(0);
                        imageSettingsBarLayout.setVisibility(View.VISIBLE);
                        imageSettingsType = ImageSettingsTypes.SATURATION;
                        setEnableBottomMenu(false);
                        break;

                    case R.id.image_processing_hue:
                        imageSettingsTitleView.setText(getResources().getString(R.string.title_image_hue));
                        imageSettingsBarView.setMin(0);
                        imageSettingsBarView.setMax(10);
                        imageSettingsBarView.setProgress(0);
                        imageSettingsBarLayout.setVisibility(View.VISIBLE);
                        imageSettingsType = ImageSettingsTypes.HUE;
                        setEnableBottomMenu(false);
                        break;
                }
                return true;
            }
        });
        imageSettingsBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(imageSettingsType != null){
                    Log.d("MY LOGS", imageSettingsType.toString());
                    switch (imageSettingsType){
                        case BRIGHTNESS:
                            viewModel.renderBrightness(seekBar.getProgress());
                            break;
                        case CONTRAST:
                            viewModel.renderContrast(seekBar.getProgress());
                            break;
                        case SATURATION:
                            viewModel.renderSaturation(seekBar.getProgress());
                            break;
                        case HUE:
                            viewModel.renderHue(seekBar.getProgress());
                            break;
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                acceptChangesButton.setVisibility(View.VISIBLE);
                denyChangesButton.setVisibility(View.VISIBLE);
                saveImageButton.setVisibility(View.GONE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        tempImageLiveData.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                imageProcessingPreview.setImageBitmap(bitmap);
            }
        });
        acceptChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptChangesButton.setVisibility(View.GONE);
                denyChangesButton.setVisibility(View.GONE);
                imageSettingsBarLayout.setVisibility(View.GONE);
                setEnableBottomMenu(true);
                saveImageButton.setVisibility(View.VISIBLE);
                viewModel.acceptChanges();
            }
        });
        denyChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptChangesButton.setVisibility(View.GONE);
                denyChangesButton.setVisibility(View.GONE);
                imageSettingsBarLayout.setVisibility(View.GONE);
                saveImageButton.setVisibility(View.VISIBLE);
                setEnableBottomMenu(true);
                viewModel.denyChanges();
            }
        });
        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.saveChanges(getContext());
                navController.popBackStack(R.id.image_camera_fragment,false);
                navController.popBackStack();
            }
        });
        return root;
    }
    public void setEnableBottomMenu(boolean enabled){
        for(int i = 0; i < imageSettingsBottomNavigationView.getMenu().size(); i++){
            imageSettingsBottomNavigationView.getMenu().getItem(i).setEnabled(enabled);
        }

    }
}