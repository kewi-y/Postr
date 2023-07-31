package com.gprod.mediaio.ui.fragments.registration;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.registration.RegistrationCallback;

public class RegistrationFragment extends Fragment {

    private RegistrationViewModel viewModel;
    private EditText emailEditText,passwordEditText,usernameEditText,profilenameEditText;
    private MaterialButton registrationButton;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        View root = inflater.inflate(R.layout.registration_fragment, container, false);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        emailEditText = root.findViewById(R.id.editTextEmailAddressRegistration);
        passwordEditText = root.findViewById(R.id.editTextPasswordRegistration);
        usernameEditText = root.findViewById(R.id.editTextNameRegistration);
        profilenameEditText = root.findViewById(R.id.editTextTagRegistration);
        registrationButton = root.findViewById(R.id.buttonRegistration);
        bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String profilename = profilenameEditText.getText().toString();
                if(!email.isEmpty() && !password.isEmpty() && !username.isEmpty() && !profilename.isEmpty()){
                    viewModel.createUser(getContext(),email, password, username, profilename, new RegistrationCallback() {
                        @Override
                        public void onRegistrationSuccess() {
                            navController.navigate(R.id.action_registrationFragment_to_navigation_main);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onRegistrationFailed() {
                            //TODO: Custom notification
                        }
                    });
                }
            }
        });
        return root;
    }

}