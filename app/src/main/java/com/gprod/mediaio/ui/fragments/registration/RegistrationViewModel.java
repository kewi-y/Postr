package com.gprod.mediaio.ui.fragments.registration;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.services.registration.RegistrationCallback;
import com.gprod.mediaio.repositories.UserRepository;


public class RegistrationViewModel extends ViewModel {
    private UserRepository userRepository;
    public RegistrationViewModel(){
        userRepository = UserRepository.getInstance();
    }
    public void createUser(Context context, String email, String password, String username, String profilename, RegistrationCallback registrationCallback){
        userRepository.registerUser(context,email,password,username,profilename,registrationCallback);
    }
}