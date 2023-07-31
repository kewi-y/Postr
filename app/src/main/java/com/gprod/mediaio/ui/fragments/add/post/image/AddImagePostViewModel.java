package com.gprod.mediaio.ui.fragments.add.post.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.interfaces.services.database.GettingPostListCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.interfaces.services.database.WritingPostCallback;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.TempPhotoRepository;
import com.gprod.mediaio.repositories.UserRepository;

import java.util.ArrayList;

public class AddImagePostViewModel extends ViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;

    private TempPhotoRepository tempPhotoRepository;
    private ArrayList<Bitmap> attachedImageList = new ArrayList<>();

    private MutableLiveData<Bitmap> postImageData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Bitmap>> attachedImageListLiveData = new MutableLiveData<>();
    public AddImagePostViewModel(){
        userRepository = UserRepository.getInstance();
        postRepository = PostRepository.getInstance();
        tempPhotoRepository = TempPhotoRepository.getInstance();
    }
    public void addPost(ArrayList<Bitmap> imageList, String description, WritingPostCallback writingPostCallback){
        Log.d("MY LOGS","Add post method");
        postRepository.writeImagePost(userRepository.getUser().getId(), description, imageList, new WritingPostCallback() {
            @Override
            public void OnSuccess() {
                Log.d("MY LOGS","Call getPostList in addPost");
                postRepository.getPostListByAuthorId(userRepository.getUser().getId(), new GettingPostListCallback() {
                    @Override
                    public void onSuccess(ArrayList<Post> posts) {
                        Log.d("MY LOGS","Call update form addPost");
                        User updatableUser = userRepository.getUser();
                        updatableUser.setPublicationsCount(posts.size());
                        userRepository.updateUser(updatableUser, new UpdatingUserCallback() {
                            @Override
                            public void onSuccess(User updatedUser) {
                                writingPostCallback.OnSuccess();
                            }

                            @Override
                            public void onFailure(String textError) {
                                writingPostCallback.OnFailure();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        writingPostCallback.OnFailure();
                    }
                });
            }
            @Override
            public void OnFailure() {
                writingPostCallback.OnFailure();
            }
        });
    }
    private void attachImage(Bitmap image){
        attachedImageList.add(image);
        attachedImageListLiveData.setValue(attachedImageList);
    }
    public void updateData(){
        if(tempPhotoRepository.getTempImageBitmap() != null){
            attachImage(tempPhotoRepository.getTempImageBitmap());
            tempPhotoRepository.clearTempImage();
        }
    }
    public void selectImage(Context context,Bitmap image){
        tempPhotoRepository.setTempImage(context,image);
    }
    public void addTags(String description){
        ArrayList<String> stringTagList = getStringTagsInDescription(description);
        for(String stringTag : stringTagList){
            postRepository.addTag(stringTag);
        }
    }

    private ArrayList<String> getStringTagsInDescription(String description){
        ArrayList<String> stringTagList = new ArrayList<>();
        String[] wordsOfDescription = description.split(" ");
        for(String word : wordsOfDescription){
            if(word.contains("#")){
                String[] stringTagsInWord = word.split("#");
                for(String tag : stringTagsInWord){
                    if(!tag.isEmpty()) {
                        stringTagList.add("#"+tag);
                    }
                }
            }
        }
        return stringTagList;
    }
    public LiveData<Bitmap> getPostImageData(){
        return postImageData;
    }

    public MutableLiveData<ArrayList<Bitmap>> getAttachedImageListLiveData() {
        return attachedImageListLiveData;
    }
}