package com.gprod.mediaio.services.story;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gprod.mediaio.enums.story.StoryType;
import com.gprod.mediaio.interfaces.services.story.AddingStoryCallback;
import com.gprod.mediaio.interfaces.services.story.GettingStoriesByAuthorId;
import com.gprod.mediaio.interfaces.services.story.GettingStoryByIdCallback;
import com.gprod.mediaio.interfaces.services.story.StoryApi;
import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.models.story.Story;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoryApiService {
    private static StoryApiService instance;
    private StoryApi storyApi;
    public static StoryApiService getInstance(String apiUrl) {
        if(instance == null){
            instance = new StoryApiService(apiUrl);
        }
        return instance;
    }
    private StoryApiService(String apiUrl){
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(apiUrl).build();
        storyApi = retrofit.create(StoryApi.class);
    }
    public void getStoryById(String id, GettingStoryByIdCallback gettingStoryByIdCallback){
        Call<Map<String, Object>> getStoryCall = storyApi.getStoryById(id);
        getStoryCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if(response.isSuccessful()){
                    Map<String, Object> storyMap = response.body();
                    Story story = getStoryFromMap(storyMap);
                    gettingStoryByIdCallback.onSuccess(story);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                gettingStoryByIdCallback.onFailure();
            }
        });
    }
    public void addImageStory(ImageStory imageStory, AddingStoryCallback addingStoryCallback){
        Call<String> addStoryCall = storyApi.addImageStory(imageStory);
        addStoryCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("MY TAG",response.toString());
                if(response.isSuccessful()){
                    addingStoryCallback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                addingStoryCallback.onFailure();
                t.printStackTrace();
            }
        });
    }
    public void getStoriesIdByAuthorId(String authorId, GettingStoriesByAuthorId gettingStoriesByAuthorId){
        Call<ArrayList<String>> getStoriesIdByAuthorIdCall = storyApi.getStoriesByAuthorId(authorId);
        getStoriesIdByAuthorIdCall.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.isSuccessful()){
                    gettingStoriesByAuthorId.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                gettingStoriesByAuthorId.onFailure();
            }
        });
    }
    private Story getStoryFromMap(Map<String, Object> storyMap){
        Story story;
        if(storyMap.get("storyType").equals(StoryType.IMAGE_STORY.toString())){
            story = new ImageStory((String) storyMap.get("id"),(String) storyMap.get("authorId"),
                    (String)storyMap.get("downloadImageUri"),(Timestamp)storyMap.get("timestamp"));
            return story;
        }
        else if(storyMap.get("storyType").equals(StoryType.VIDEO_STORY.toString())){
            return null;
        }
        else {
            return null;
        }
    }
}
