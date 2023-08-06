package com.gprod.mediaio.repositories;

import android.graphics.Bitmap;
import android.net.Uri;

import com.gprod.mediaio.interfaces.repositories.story.GettingStoryListByAuthorIdCallback;
import com.gprod.mediaio.interfaces.repositories.story.UploadFileProgressCallback;
import com.gprod.mediaio.interfaces.services.storage.UploadFileCallback;
import com.gprod.mediaio.interfaces.services.story.AddingStoryCallback;
import com.gprod.mediaio.interfaces.services.story.GettingStoriesByAuthorId;
import com.gprod.mediaio.interfaces.services.story.GettingStoryByIdCallback;
import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.models.story.Story;
import com.gprod.mediaio.models.story.VideoStory;
import com.gprod.mediaio.services.storage.firebase.FirebaseStorageService;
import com.gprod.mediaio.services.story.StoryApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StoryRepository {
    private static StoryRepository instance;
    private StoryApiService storyApiService;
    private FirebaseStorageService storage;
    public static StoryRepository getInstance(String apiUri) {
        if(instance == null){
            instance = new StoryRepository(apiUri);
        }
        return instance;
    }
    private StoryRepository(String apiUrl){
        storyApiService = StoryApiService.getInstance(apiUrl);
        storage = FirebaseStorageService.getInstance();
    }
    public void getStoryListByAuthorId(String authorId,GettingStoryListByAuthorIdCallback gettingStoryListByAuthorIdCallback){
        storyApiService.getStoriesIdByAuthorId(authorId, new GettingStoriesByAuthorId() {
            @Override
            public void onSuccess(ArrayList<String> storiesId) {
                for(String storyId : storiesId){
                    Story[] storyArray = new Story[storiesId.size()];
                    storyApiService.getStoryById(storyId, new GettingStoryByIdCallback() {
                        @Override
                        public void onSuccess(Story story) {
                            storyArray[storiesId.indexOf(storyId)] = story;
                            if(Arrays.stream(storyArray).noneMatch(Objects::isNull)){
                                List<Story> storyList = Arrays.asList(storyArray);
                                gettingStoryListByAuthorIdCallback.onSuccess((ArrayList<Story>) storyList);
                            }
                        }

                        @Override
                        public void onFailure() {
                            gettingStoryListByAuthorIdCallback.onFailure();
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
                gettingStoryListByAuthorIdCallback.onFailure();
            }
        });
    }
    public void addImageStory(Bitmap image, String authorId, UploadFileProgressCallback uploadFileProgressCallback, AddingStoryCallback addingStoryCallback){
        UUID imageId = UUID.randomUUID();
        storage.uploadImage(image, imageId.toString(), new UploadFileCallback() {
            @Override
            public void onSuccess(Uri downloadUri) {
                UUID storyId = UUID.randomUUID();
                ImageStory imageStory = new ImageStory(storyId.toString(),authorId,downloadUri.toString(),null);
                storyApiService.addImageStory(imageStory, new AddingStoryCallback() {
                    @Override
                    public void onSuccess() {
                        addingStoryCallback.onSuccess();
                    }

                    @Override
                    public void onFailure() {
                        addingStoryCallback.onFailure();
                    }
                });
            }

            @Override
            public void onProgress(int percent) {
                uploadFileProgressCallback.onProgress(percent);
            }

            @Override
            public void onFailure() {
                addingStoryCallback.onFailure();
            }
        });
    }
    public void addVideoStory(byte[] file, String authorId, UploadFileProgressCallback uploadFileProgressCallback, AddingStoryCallback addingStoryCallback){
        UUID videoId = UUID.randomUUID();
        storage.uploadVideo(file, String.valueOf(videoId), new UploadFileCallback() {
            @Override
            public void onSuccess(Uri downloadUri) {
                UUID videoStoryId = UUID.randomUUID();
                VideoStory videoStory = new VideoStory(String.valueOf(videoStoryId),authorId,downloadUri.toString(),null);
                storyApiService.addVideoStory(videoStory, new AddingStoryCallback() {
                    @Override
                    public void onSuccess() {
                        addingStoryCallback.onSuccess();
                    }

                    @Override
                    public void onFailure() {
                        addingStoryCallback.onFailure();
                    }
                });
            }

            @Override
            public void onProgress(int percent) {
                uploadFileProgressCallback.onProgress(percent);
            }

            @Override
            public void onFailure() {
                addingStoryCallback.onFailure();
            }
        });
    }
}
