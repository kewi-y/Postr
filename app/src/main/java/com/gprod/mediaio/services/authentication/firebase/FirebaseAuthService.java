package com.gprod.mediaio.services.authentication.firebase;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthService {
    private static FirebaseAuthService instance;
    private FirebaseAuth auth;
    public static FirebaseAuthService getInstance(){
        if(instance == null){
            instance = new FirebaseAuthService();
        }
        return instance;
    }
    private FirebaseAuthService(){
        auth = FirebaseAuth.getInstance();
    }
    public boolean checkAuth(){
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            return false;
        }
        return true;
    }
    public void signInWithEmailAndPassword(String email, String password, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener){
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void createUserWithEmailAndPassword(String email, String password, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener){
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void exitFromAccount(){
        auth.signOut();
    }
    public FirebaseUser getFirebaseUser(){
        return auth.getCurrentUser();
    }
}
