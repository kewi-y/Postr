package com.gprod.mediaio.repositoreis.story;

import android.util.Log;

import com.gprod.mediaio.interfaces.repositories.story.GettingStoryListByAuthorIdCallback;
import com.gprod.mediaio.interfaces.services.story.AddingStoryCallback;
import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.models.story.Story;
import com.gprod.mediaio.repositories.StoryRepository;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class StoryRepositoryTest {
    private static StoryRepository storyRepository;
    private static Random random;
    private static final String authorId = "121shfys";
    @Before
    public void setup(){
        storyRepository = StoryRepository.getInstance("https://5da7-146-158-10-185.ngrok-free.app/");
        random = new Random();
    }
    @Test
    public void addingImageStoryTest(){
        ImageStory imageStory = new ImageStory(String.valueOf(random.nextDouble()),authorId,"some uri",null);
        storyRepository.addImageStory(imageStory, new AddingStoryCallback() {
            @Override
            public void onSuccess() {
                assert true;
            }

            @Override
            public void onFailure() {
                assert false;
            }
        });
    }
    @Test
    public void getStoryListByIdTest(){
        storyRepository.getStoryListByAuthorId(authorId, new GettingStoryListByAuthorIdCallback() {
            @Override
            public void onSuccess(ArrayList<Story> storyList) {
                storyList.forEach(new Consumer<Story>() {
                    @Override
                    public void accept(Story story) {
                        Log.d("MY LOGS", "Story id >>: " + story.getId());
                    }
                });
                assert true;
            }

            @Override
            public void onFailure() {
                assert false;
            }
        });
    }
}
