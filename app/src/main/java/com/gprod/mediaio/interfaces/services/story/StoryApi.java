package com.gprod.mediaio.interfaces.services.story;

import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.models.story.VideoStory;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StoryApi {
    @GET("getStoryById")
    Call<Map<String, Object>> getStoryById(@Query("storyId") String id);
    @GET("getStoriesByAuthorId")
    Call<ArrayList<String>> getStoriesByAuthorId(@Query("authorId") String authorId);
    @POST("addImageStory")
    Call<String> addImageStory(@Body ImageStory imageStory);
    @POST("addVideoStory")
    Call<String> addVideoStory(@Body VideoStory videoStory);

}
