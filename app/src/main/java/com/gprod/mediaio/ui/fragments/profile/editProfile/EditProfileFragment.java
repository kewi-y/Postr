package com.gprod.mediaio.ui.fragments.profile.editProfile;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.dialogs.imageSourceDialog.ChooseImageSourceDialogCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;
import com.gprod.mediaio.ui.dialogs.imageSource.ChooseImageSourceDialog;

public class EditProfileFragment extends Fragment {

    private EditProfileViewModel viewModel;

    private LiveData<User> userLiveData;
    private LiveData<Uri> tempImageUriLivaData;
    private MaterialButton confirmChangesButton;
    private EditText editBioView,editUsernameView,editProfilenameView;
    private TextView editProfilePhotoTextView;
    private SimpleDraweeView editProfilePhotoView;
    private NavController navController;
    private NavHostFragment navHostFragment;
    private ImageButton logOutButton;
    private ChooseImageSourceDialog chooseImageSourceDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        userLiveData = viewModel.getUserLiveData();
        tempImageUriLivaData = viewModel.getTempImageUriLiveData();
        confirmChangesButton = root.findViewById(R.id.buttonConfirmChanges);
        editBioView = root.findViewById(R.id.editProfileBioView);
        editUsernameView = root.findViewById(R.id.editProfileNameView);
        editProfilenameView = root.findViewById(R.id.editProfileTagView);
        editProfilePhotoTextView = root.findViewById(R.id.editProfilePhotoTextView);
        editProfilePhotoView = root.findViewById(R.id.editProfilePhotoView);
        logOutButton = root.findViewById(R.id.logOutButton);
        chooseImageSourceDialog = ChooseImageSourceDialog.getInstance(getActivity());
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                navController.popBackStack();

                this.remove();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        userLiveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                editBioView.setText(user.getBio());
                editProfilenameView.setText(user.getProfilename());
                editUsernameView.setText(user.getUsername());
                editProfilePhotoView.setImageURI(user.getProfilePhotoDownloadUri());
            }
        });
        tempImageUriLivaData.observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                editProfilePhotoView.setImageURI(uri);
            }
        });
        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingPopup.show(getContext());
                String bio,username,profilename;
                bio = editBioView.getText().toString();
                username = editUsernameView.getText().toString();
                profilename = editProfilenameView.getText().toString();
                if(!bio.isEmpty() && !username.isEmpty() && !profilename.isEmpty()){
                    viewModel.updateUserInfo(username, profilename, bio, new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            LoadingPopup.hide(getContext());
                            onBackPressedCallback.remove();
                            navController.navigate(R.id.navigation_profile);
                        }

                        @Override
                        public void onFailure(String textError) {
                            LoadingPopup.hide(getContext());
                            NotificationPopup.show(getContext(),true,textError);
                        }
                    });
                }
            }
        });

        editProfilePhotoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageSourceDialog.show(new ChooseImageSourceDialogCallback() {
                    @Override
                    public void onChooseCamera(){
                        onBackPressedCallback.remove();
                        navController.navigate(R.id.image_camera_fragment);
                    }
                    @Override
                    public void onChooseGallery() {
                        NotificationPopup.show(getContext(),true,"В разработке");
                    }
                });
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressedCallback.remove();
                viewModel.exitFromAccount();
                navController.navigate(R.id.action_global_authFragment);
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
        viewModel.loadData(getContext());
        return root;
    }

}