package com.gprod.mediaio.ui.fragments.auth;

import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.services.authentication.AuthenticationCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.NfcParsedUserRepository;
import com.gprod.mediaio.repositories.SelectedUserRepository;
import com.gprod.mediaio.repositories.UserRepository;

public class AuthViewModel extends ViewModel {
    private UserRepository userRepository;
    public AuthViewModel(){
        userRepository = UserRepository.getInstance();
    }
    public boolean checkAuth(){
        return userRepository.checkAuth();
    }

    public void authUser(String email, String password, AuthenticationCallback authenticationCallback){
        userRepository.authUser(email, password, authenticationCallback);
    }
    public void autoAuthUser(AuthenticationCallback authenticationCallback){
        userRepository.autoAuthUser(authenticationCallback);
    }
}