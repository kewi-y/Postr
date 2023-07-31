package com.gprod.mediaio.ui.fragments.auth;

import androidx.lifecycle.LiveData;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.authentication.AuthenticationCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

public class AuthFragment extends Fragment {

    private AuthViewModel viewModel;
    private EditText emailEditText,passwordEditText;
    private MaterialButton authButton;

    private TextView registrationTextView;
    private NavController navController;
    private LiveData<User> userLiveData;
    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        View root = inflater.inflate(R.layout.auth_fragment, container, false);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        emailEditText = root.findViewById(R.id.editTextEmailAddress);
        passwordEditText = root.findViewById(R.id.editTextPassword);
        registrationTextView = root.findViewById(R.id.registrationTextView);
        authButton = root.findViewById(R.id.buttonAuth);
        bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        if(viewModel.checkAuth()){
            viewModel.autoAuthUser(new AuthenticationCallback() {
                @Override
                public void onSuccess() {
                    navController.navigate(R.id.action_authFragment_to_navigation_main);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure() {

                }
            });
        }
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingPopup.show(getContext());
                authButton.setClickable(false);
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()){
                    viewModel.authUser(email, password, new AuthenticationCallback() {
                        @Override
                        public void onSuccess() {
                            navController.navigate(R.id.action_authFragment_to_navigation_main);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure() {
                            LoadingPopup.hide(getContext());
                            NotificationPopup.show(getContext(),true,"не удается войти в аккаунт");
                            //TODO: Migrate string in func to res
                        }
                    });
                }
            }
        });
        registrationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_authFragment_to_registrationFragment);
            }
        });
        return root;
    }

}