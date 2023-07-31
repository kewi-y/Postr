package com.gprod.mediaio.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.interfaces.repositories.user.AddAlbumCallback;
import com.gprod.mediaio.interfaces.repositories.user.SubscribeCallback;
import com.gprod.mediaio.interfaces.repositories.user.UnsubscribeCallback;
import com.gprod.mediaio.interfaces.repositories.user.UpdatingAlbumCallback;
import com.gprod.mediaio.interfaces.services.database.CheckingForUniqueCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.interfaces.services.authentication.AuthenticationCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.interfaces.services.registration.RegistrationCallback;
import com.gprod.mediaio.interfaces.services.storage.GettingFileDownloadUriCallback;
import com.gprod.mediaio.interfaces.services.storage.UploadFileCallback;
import com.gprod.mediaio.models.album.Album;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.services.authentication.firebase.FirebaseAuthService;
import com.gprod.mediaio.services.database.firebase.FirebaseDatabaseService;
import com.gprod.mediaio.services.storage.firebase.FirebaseStorageService;

import java.util.ArrayList;
import java.util.UUID;

public class UserRepository {
    private static UserRepository instance;
    private FirebaseDatabaseService firebaseDatabaseService;
    private FirebaseAuthService firebaseAuthService;
    private FirebaseStorageService firebaseStorageService;
    private User user;
    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }
    private UserRepository(){
        firebaseDatabaseService = FirebaseDatabaseService.getInstance();
        firebaseAuthService = FirebaseAuthService.getInstance();
        firebaseStorageService = FirebaseStorageService.getInstance();
    }

    public boolean checkAuth(){
        return firebaseAuthService.checkAuth();
    }

    public void registerUser(Context context, String email, String password, String username, String profilename, RegistrationCallback registrationCallback){
        firebaseAuthService.createUserWithEmailAndPassword(email, password, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if(firebaseAuthService.checkAuth()){
                    firebaseStorageService.getImageDownloadUri(context.getResources().getString(R.string.default_profile_picture_id), new GettingFileDownloadUriCallback() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user  = new User(firebaseAuthService.getFirebaseUser().getUid(),
                                    username,
                                    profilename,
                                    "Bio not have yet",
                                    uri.toString());
                            user.setSubscribers(new ArrayList<>());
                            user.setSubscriptions(new ArrayList<>());
                            user.setFavoritesPostList(new ArrayList<>());
                            user.setSubscriptionsCount(0);
                            user.setPublicationsCount(0);
                            user.setSubscribersCount(0);
                            firebaseDatabaseService.writeUser(user, new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    registrationCallback.onRegistrationSuccess();
                                }
                            });
                        }
                        @Override
                        public void onFailure() {
                            registrationCallback.onRegistrationFailed();
                        }
                    });

                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                registrationCallback.onRegistrationFailed();
            }
        });
    }

    public void authUser(String email, String password, AuthenticationCallback authenticationCallback){
        firebaseAuthService.signInWithEmailAndPassword(email, password, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if(firebaseAuthService.checkAuth()){
                    firebaseDatabaseService.getUserById(firebaseAuthService.getFirebaseUser().getUid(), new GettingUserByIdCallback() {
                        @Override
                        public void onSuccess(User user) {
                            instance.user = user;
                            Log.d("MY LOGS","Change User Class in user Repository");
                            Log.d("MY LOGS","catch user id: " + user.getId());
                            authenticationCallback.onSuccess();
                        }

                        @Override
                        public void onFailure() {
                            authenticationCallback.onFailure();
                        }
                    });
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                authenticationCallback.onFailure();
            }
        });
    }

    public void autoAuthUser(AuthenticationCallback authenticationCallback){
        firebaseDatabaseService.getUserById(firebaseAuthService.getFirebaseUser().getUid(), new GettingUserByIdCallback() {
            @Override
            public void onSuccess(User user) {
                instance.user = user;
                authenticationCallback.onSuccess();
            }

            @Override
            public void onFailure() {
                authenticationCallback.onFailure();
            }
        });
    }

    public User getUser(){return instance.user;}

    public void updateUser(User user,UpdatingUserCallback updatingUserCallback){
        firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                instance.user = user;
                updatingUserCallback.onSuccess(updatedUser);
            }

            @Override
            public void onFailure(String textError) {
                updatingUserCallback.onFailure(textError);
            }
        });
    }
    public void uploadProfilePhoto(Bitmap image, UploadFileCallback uploadFileCallback){
        String profilePhotoId = UUID.randomUUID().toString();
        firebaseStorageService.uploadImage(image, profilePhotoId, new UploadFileCallback() {
            @Override
            public void onSuccess(Uri downloadUri) {
                instance.user.setProfilePhotoDownloadUri(downloadUri.toString());
                firebaseDatabaseService.updateUser(instance.user, new UpdatingUserCallback() {
                    @Override
                    public void onSuccess(User updatedUser) {
                        uploadFileCallback.onSuccess(downloadUri);
                    }

                    @Override
                    public void onFailure(String textError) {
                        uploadFileCallback.onFailure();
                    }
                });
            }
            @Override
            public void onFailure() {
                uploadFileCallback.onFailure();
            }
        });
    }
    public void getUserByID(String id, GettingUserByIdCallback gettingUserByIdCallback){
        firebaseDatabaseService.getUserById(id,gettingUserByIdCallback);
    }
    public void checkProfilenameForUnique(String profilename, CheckingForUniqueCallback checkingForUniqueCallback){
        firebaseDatabaseService.checkProfilenameForUnique(profilename, checkingForUniqueCallback);
    }
    public void exitFormAccount(){
        firebaseAuthService.exitFromAccount();
    }

    public void subscribeToUser(User subscriptionUser, SubscribeCallback subscribeCallback){
        if(user.getSubscriptions() != null){
            user.getSubscriptions().add(subscriptionUser.getId());
            user.setSubscriptionsCount(user.getSubscriptions().size());
        }
        else {
            ArrayList<String> userSubscriptions = new ArrayList<>();
            userSubscriptions.add(subscriptionUser.getId());
            user.setSubscriptions(userSubscriptions);
            user.setSubscriptionsCount(userSubscriptions.size());
        }
        if(subscriptionUser.getSubscribers() != null){
            subscriptionUser.getSubscribers().add(user.getId());
            subscriptionUser.setSubscribersCount(subscriptionUser.getSubscribers().size());
        }
        else {
            ArrayList<String> subscriptionUserSubscribers = new ArrayList<>();
            subscriptionUserSubscribers.add(user.getId());
            subscriptionUser.setSubscribers(subscriptionUserSubscribers);
            subscriptionUser.setSubscribersCount(subscriptionUserSubscribers.size());
        }
        firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                firebaseDatabaseService.updateUser(subscriptionUser, new UpdatingUserCallback() {
                    @Override
                    public void onSuccess(User updatedUser) {
                        subscribeCallback.onSuccess();
                    }

                    @Override
                    public void onFailure(String textError) {
                        subscribeCallback.onFailure();
                    }
                });
            }

            @Override
            public void onFailure(String textError) {
                subscribeCallback.onFailure();
            }
        });
    }
    public void unsubscribeToUser(User unsubscribeUser, UnsubscribeCallback unsubscribeCallback){
        if(unsubscribeUser.getSubscribers() != null){
            if(user.getSubscriptions() != null){
                unsubscribeUser.getSubscribers().remove(user.getId());
                unsubscribeUser.setSubscribersCount(unsubscribeUser.getSubscribers().size());
                user.getSubscriptions().remove(unsubscribeUser.getId());
                user.setSubscriptionsCount(user.getSubscriptions().size());
                firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
                    @Override
                    public void onSuccess(User updatedUser) {
                        firebaseDatabaseService.updateUser(unsubscribeUser, new UpdatingUserCallback() {
                            @Override
                            public void onSuccess(User updatedUser) {
                                unsubscribeCallback.onSuccess();
                            }
                            @Override
                            public void onFailure(String textError) {
                                unsubscribeCallback.onFailure();
                            }
                        });
                    }
                    @Override
                    public void onFailure(String textError) {
                        unsubscribeCallback.onFailure();
                    }
                });
            }
        }
    }
    public void addPostToFavorites(Post post, UpdatingUserCallback updatingUserCallback){
        if(user.getFavoritesPostList() == null){
            user.setFavoritesPostList(new ArrayList<>());
            user.getFavoritesPostList().add(post.getPostId());
        }
        else {
            user.getFavoritesPostList().add(post.getPostId());
        }
        firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                user = updatedUser;
                updatingUserCallback.onSuccess(updatedUser);
            }

            @Override
            public void onFailure(String textError) {
                updatingUserCallback.onFailure(textError);
            }
        });

    }
    public void addAlbum(Post firstPost, String albumName, AddAlbumCallback addAlbumCallback){
        String albumId = UUID.randomUUID().toString();
        ArrayList<String> postIdList = new ArrayList<>();
        if(user.getFavoritesAlbumList() == null) {
            user.setFavoritesAlbumList(new ArrayList<>());
        }
        postIdList.add(firstPost.getPostId());
        if(firstPost.getPostType().equals(PostTypes.IMAGE_POST)){
            user.getFavoritesAlbumList().add(new Album(albumId,albumName,postIdList,
                    ((ImagePost)firstPost).getPostImageDownloadUriList().get(0)));
        }
        firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                user = updatedUser;
                addAlbumCallback.onSuccess(albumId);
            }

            @Override
            public void onFailure(String textError) {addAlbumCallback.onFailure();}
        });
    }
    public void addPostToAlbum(Post post,String albumId, UpdatingAlbumCallback updatingAlbumCallback){
        if(user.getFavoritesAlbumList() != null) {
            for (Album album : user.getFavoritesAlbumList()) {
                if (albumId.equals(album.getId())) {
                    album.getPostIdList().add(post.getPostId());
                    firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            user = updatedUser;
                            updatingAlbumCallback.onSuccess(album);
                        }

                        @Override
                        public void onFailure(String textError) {
                            updatingAlbumCallback.onFailure();
                        }
                    });
                }
            }
        }
    }
    public void removePostFromFavorites(Post post,UpdatingUserCallback updatingUserCallback){
        if(user.getFavoritesPostList() != null){
            user.getFavoritesPostList().remove(post.getPostId());
            firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
                @Override
                public void onSuccess(User updatedUser) {
                    user = updatedUser;
                    updatingUserCallback.onSuccess(updatedUser);
                }

                @Override
                public void onFailure(String textError) {
                    updatingUserCallback.onFailure(textError);
                }
            });
        }
    }
    public void removePostFromAlbum(Post post, String albumId, UpdatingAlbumCallback updatingAlbumCallback){
        if(user.getFavoritesAlbumList() != null){
            for(Album album : user.getFavoritesAlbumList()){
                if(album.getId().equals(albumId) && album.getPostIdList().contains(post.getPostId())){
                    album.getPostIdList().remove(post.getPostId());
                    firebaseDatabaseService.updateUser(user, new UpdatingUserCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            user = updatedUser;
                            updatingAlbumCallback.onSuccess(album);
                        }

                        @Override
                        public void onFailure(String textError) {
                            updatingAlbumCallback.onFailure();
                        }
                    });
                }
            }
        }
    }
}
