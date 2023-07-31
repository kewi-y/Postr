package com.gprod.mediaio.repositories;

import com.gprod.mediaio.interfaces.repositories.story.GettingStoryListByAuthorIdCallback;
import com.gprod.mediaio.interfaces.services.story.AddingStoryCallback;
import com.gprod.mediaio.interfaces.services.story.GettingStoriesByAuthorId;
import com.gprod.mediaio.interfaces.services.story.GettingStoryByIdCallback;
import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.models.story.Story;
import com.gprod.mediaio.services.story.StoryApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StoryRepository {
    private static StoryRepository instance;
    private StoryApiService storyApiService;
    public static StoryRepository getInstance(String apiUrl) {
        if(instance == null){
            instance = new StoryRepository(apiUrl);
        }
        return instance;
    }
    private StoryRepository(String apiUrl){
        storyApiService = StoryApiService.getInstance(apiUrl);
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
    public void addImageStory(ImageStory imageStory, AddingStoryCallback addingStoryCallback){
        storyApiService.addImageStory(imageStory,addingStoryCallback);
    }
}
