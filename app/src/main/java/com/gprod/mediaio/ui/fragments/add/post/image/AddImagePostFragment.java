package com.gprod.mediaio.ui.fragments.add.post.image;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.AttachedImageListAdapter;
import com.gprod.mediaio.interfaces.dialogs.imageSourceDialog.ChooseImageSourceDialogCallback;
import com.gprod.mediaio.interfaces.services.database.WritingPostCallback;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;
import com.gprod.mediaio.ui.dialogs.imageSource.ChooseImageSourceDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class AddImagePostFragment extends Fragment {

    private AddImagePostViewModel viewModel;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private EditText descriptionEditText;
    private MaterialButton addPostButton;
    private ImageButton addImageButton;
    private LiveData<Bitmap> postImageData;
    private RecyclerView attachedImageListView;
    private LiveData<ArrayList<Bitmap>> attachedImageListLiveData;
    private Uri cameraImageUri;
    private ChooseImageSourceDialog chooseImageSourceDialog;
    private AttachedImageListAdapter attachedImageListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AddImagePostViewModel.class);
        View root = inflater.inflate(R.layout.add_image_post_fragment, container, false);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        descriptionEditText = root.findViewById(R.id.descriptionEditText);
        addPostButton = root.findViewById(R.id.buttonAddPost);
        chooseImageSourceDialog = ChooseImageSourceDialog.getInstance(getActivity());
        addImageButton = root.findViewById(R.id.addImageButton);
        attachedImageListView = root.findViewById(R.id.attachedImageListView);
        attachedImageListView.setLayoutManager(new GridLayoutManager(getContext(),3));
        attachedImageListLiveData = viewModel.getAttachedImageListLiveData();
        ActivityResultLauncher cameraActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    if(cameraImageUri != null){
                        try {
                            Bitmap image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),cameraImageUri);
                            viewModel.selectImage(getContext(),image);
                            navController.navigate(R.id.imageProcessingFragment);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        ActivityResultLauncher pickImageActivityLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result != null) {
                    try {
                        Bitmap image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result);
                        viewModel.selectImage(getContext(), image);
                        navController.navigate(R.id.imageProcessingFragment);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        attachedImageListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Bitmap>>() {
            @Override
            public void onChanged(ArrayList<Bitmap> images) {
                if(attachedImageListAdapter == null){
                    attachedImageListAdapter = new AttachedImageListAdapter(getContext(),images);
                    attachedImageListView.setAdapter(attachedImageListAdapter);
                }
                else {
                    attachedImageListAdapter.updateAttachedImageList(images);
                }
            }
        });
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageSourceDialog.show(new ChooseImageSourceDialogCallback() {
                    @Override
                    public void onChooseCamera() {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE,getString(R.string.temp_photo_name));
                        cameraImageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                        Intent cameraActivityIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraActivityIntent.putExtra(MediaStore.EXTRA_OUTPUT,cameraImageUri);
                        cameraActivityLauncher.launch(cameraActivityIntent);
                    }

                    @Override
                    public void onChooseGallery() {
                        pickImageActivityLauncher.launch("image/*");
                    }
                });
            }
        });
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(attachedImageListLiveData.getValue() != null && attachedImageListLiveData.getValue().size() != 0) {
                    if(!descriptionEditText.getText().toString().isEmpty()) {
                        LoadingPopup.show(getContext());
                        viewModel.addTags(descriptionEditText.getText().toString());
                        viewModel.addPost(attachedImageListLiveData.getValue(), descriptionEditText.getText().toString(), new WritingPostCallback() {
                            @Override
                            public void OnSuccess() {
                                LoadingPopup.hide(getContext());
                                navController.navigate(R.id.navigation_profile);
                                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void OnFailure() {
                                LoadingPopup.hide(getContext());
                                navController.navigate(R.id.navigation_home);
                                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else{
                        NotificationPopup.show(getContext(),false,"Пожалуйста, добавьте описание");
                        //TODO:migrate string in show func to resources
                    }
                }
                else{
                    NotificationPopup.show(getContext(),false,"Пожалуйста добавьте изображение");
                    //TODO:migrate string in show func to resources
                }
            }
        });
        viewModel.updateData();
        return root;
    }
}