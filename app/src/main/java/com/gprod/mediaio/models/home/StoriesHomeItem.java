package com.gprod.mediaio.models.home;

import android.content.Context;

import com.gprod.mediaio.adapters.StoryItemListAdapter;
import com.gprod.mediaio.enums.home.HomeItemTypes;
import com.gprod.mediaio.interfaces.home.StoryItemClickListener;
import com.gprod.mediaio.models.story.Story;
import com.gprod.mediaio.models.story.StoryItem;

import java.util.ArrayList;

public class StoriesHomeItem extends HomeItem{
    private ArrayList<StoryItem> storyItems;
    private StoryItemListAdapter storyItemListAdapter;
    private StoryItemClickListener storyItemClickListener;
    public StoriesHomeItem(Context context,StoryItemClickListener storyItemClickListener) {
        super(HomeItemTypes.STORIES_ITEM);
        this.storyItemClickListener = storyItemClickListener;
        storyItemListAdapter = new StoryItemListAdapter(context,storyItemClickListener);
    }

    public ArrayList<StoryItem> getStoryItems() {
        return storyItems;
    }

    public StoryItemClickListener getStoryItemClickListener() {
        return storyItemClickListener;
    }

    public void setStoryItems(Context context,ArrayList<StoryItem> storyItems) {
        this.storyItems = storyItems;
        if(storyItemListAdapter != null){
            storyItemListAdapter.updateStoryItems(storyItems);
        }
        else {
            storyItemListAdapter = new StoryItemListAdapter(context, storyItemClickListener);
            storyItemListAdapter.updateStoryItems(storyItems);
        }
    }

    public StoryItemListAdapter getStoryItemListAdapter() {
        return storyItemListAdapter;
    }
}
