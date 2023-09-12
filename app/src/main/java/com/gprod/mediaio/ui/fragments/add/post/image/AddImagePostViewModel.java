package com.gprod.mediaio.ui.fragments.add.post.image;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.validation.Schema;

public class AddImagePostViewModel extends ViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;

    private TempPhotoRepository tempPhotoRepository;
    private ArrayList<String> galleryImageList = new ArrayList<>();

    private MutableLiveData<Bitmap> postImageData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> galleryImageListLiveData = new MutableLiveData<>();
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
                                tempPhotoRepository.clearAttachedImageList();
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        Bitmap compressedImage = BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        tempPhotoRepository.attachImage(compressedImage);
        attachedImageListLiveData.setValue(tempPhotoRepository.getAttachedImageList());
    }
    public void updateData(){
        if(tempPhotoRepository.getTempImageBitmap() != null){
            attachImage(tempPhotoRepository.getTempImageBitmap());
            tempPhotoRepository.clearTempImage();
        }
    }
    public void clearAttachedImageList(){
        tempPhotoRepository.clearAttachedImageList();
    }
    public void detachImage(Bitmap image){
        tempPhotoRepository.detachImage(image);
        attachedImageListLiveData.setValue(tempPhotoRepository.getAttachedImageList());
    }
    public void loadGalleryImageList(Context context){
        if(galleryImageList.size() > 0){
            galleryImageList.clear();
        }
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
        while (cursor.moveToNext()){
            String stringUri = cursor.getString(0);
            File file = new File(stringUri);
            Uri uri = Uri.fromFile(file);
            stringUri = uri.toString();
            Log.d("MY LOGS", "Gallery Image Uri >>: " + stringUri);
            galleryImageList.add(stringUri);
        }
        Collections.reverse(galleryImageList);
        galleryImageListLiveData.setValue(galleryImageList);
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

    public LiveData<ArrayList<Bitmap>> getAttachedImageListLiveData() {
        return attachedImageListLiveData;
    }

    public LiveData<ArrayList<String>> getGalleryImageListLiveData() {
        return galleryImageListLiveData;
    }
}