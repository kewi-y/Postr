package com.gprod.mediaio.repositories;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gprod.mediaio.interfaces.repositories.post.UploadImageListCallback;
import com.gprod.mediaio.interfaces.services.database.CheckingForUniqueCallback;
import com.gprod.mediaio.interfaces.services.database.DeletingPostCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostByIdCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostListCallback;
import com.gprod.mediaio.interfaces.services.database.GettingTagByStringTagCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.WritingPostCallback;
import com.gprod.mediaio.interfaces.services.storage.UploadFileCallback;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Comment;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.post.Tag;
import com.gprod.mediaio.services.database.firebase.FirebaseDatabaseService;
import com.gprod.mediaio.services.storage.firebase.FirebaseStorageService;

import java.util.ArrayList;
import java.util.UUID;

public class PostRepository {
    private static PostRepository instance;
    private ArrayList<Post> feed = new ArrayList<>();
    private FirebaseStorageService firebaseStorageService;
    private FirebaseDatabaseService firebaseDatabaseService;
    public static PostRepository getInstance(){
        if(instance == null){
            instance = new PostRepository();
        }
        return instance;
    }

    private PostRepository(){
        firebaseDatabaseService = FirebaseDatabaseService.getInstance();
        firebaseStorageService = FirebaseStorageService.getInstance();
    }

    public void generateFeed(){

    }
    public void writeImagePost(String authorId,String description,ArrayList<Bitmap> imageList, WritingPostCallback writingPostCallback){
        String postId = UUID.randomUUID().toString();
        uploadImageList(imageList, new UploadImageListCallback() {
            @Override
            public void onSuccess(ArrayList<String> uploadedImageDownloadUriList) {
                ImagePost imagePost = new ImagePost(authorId,postId,uploadedImageDownloadUriList,description,
                        new ArrayList<>(), new ArrayList<>(), getStringTagsInDescription(description));
                firebaseDatabaseService.writePost(imagePost, new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        writingPostCallback.OnSuccess();
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
    private void uploadImageList(ArrayList<Bitmap> imageList, UploadImageListCallback uploadImageListCallback){
        ArrayList<String> uploadedImagesDownloadUriList = new ArrayList<>();
        for(Bitmap image : imageList){
            String imageId = UUID.randomUUID().toString();
            firebaseStorageService.uploadImage(image, imageId, new UploadFileCallback() {
                @Override
                public void onSuccess(Uri downloadUri) {
                    uploadedImagesDownloadUriList.add(downloadUri.toString());
                    if(uploadedImagesDownloadUriList.size() == imageList.size()){
                        uploadImageListCallback.onSuccess(uploadedImagesDownloadUriList);
                    }
                }

                @Override
                public void onFailure() {
                    uploadImageListCallback.onFailure();
                }
            });
        }
    }
    public void getPostListByAuthorId(String authorId, GettingPostListCallback gettingPostListCallback){
        firebaseDatabaseService.getPostListByAuthorId(authorId,gettingPostListCallback);
    }
    public void getPostById(String postId, GettingPostByIdCallback gettingPostByIdCallback){
        firebaseDatabaseService.getPostById(postId,gettingPostByIdCallback);
    }
    public void updatePost(Post post, UpdatingPostCallback updatingPostCallback){
        firebaseDatabaseService.updatePost(post,updatingPostCallback);
    }
    public void getPostListByTag(String tag,GettingPostListCallback gettingPostListCallback){
        firebaseDatabaseService.getPostListByTag(tag,gettingPostListCallback);
    }

    public void addLike(Post post, User author,UpdatingPostCallback updatingPostCallback){
        post.addLike(author.getId());
        firebaseDatabaseService.updatePost(post, new UpdatingPostCallback() {
            @Override
            public void onSuccess(Post post) {
                updatingPostCallback.onSuccess(post);
            }

            @Override
            public void onFailure() {
                updatingPostCallback.onFailure();
            }
        });
    }
    public void deletePost(Post post, DeletingPostCallback deletingPostCallback){
        firebaseDatabaseService.deletePost(post,deletingPostCallback);
    }

    public void removeLike(Post post,User author,UpdatingPostCallback updatingPostCallback){
        post.removeLike(author.getId());
        firebaseDatabaseService.updatePost(post, new UpdatingPostCallback() {
            @Override
            public void onSuccess(Post post) {
                updatingPostCallback.onSuccess(post);
            }

            @Override
            public void onFailure() {
                updatingPostCallback.onFailure();
            }
        });
    }
    public void addComment(Post post, User author, String commentContent, UpdatingPostCallback updatingPostCallback){
        String commentId = String.valueOf(UUID.randomUUID());
        Comment comment = new Comment(author,commentId,commentContent);
        post.addComment(comment);
        firebaseDatabaseService.updatePost(post, new UpdatingPostCallback() {
            @Override
            public void onSuccess(Post post) {
                updatingPostCallback.onSuccess(post);
            }

            @Override
            public void onFailure() {
                updatingPostCallback.onFailure();
            }
        });
    }
    public void addTag(String stringTag){
        firebaseDatabaseService.checkTagForUnique(stringTag, new CheckingForUniqueCallback() {
            @Override
            public void isUnique() {
                firebaseDatabaseService.addTag(stringTag);
            }

            @Override
            public void isNotUnique() {

            }
        });
    }
    public void addViewCountForTag(Tag tag){
        tag.addViewCount();
        firebaseDatabaseService.updateTag(tag);
    }
    public void getTagByStringTag(String stringTag, GettingTagByStringTagCallback gettingTagByStringTagCallback){
        firebaseDatabaseService.getTagByStringTag(stringTag,gettingTagByStringTagCallback);
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
}
