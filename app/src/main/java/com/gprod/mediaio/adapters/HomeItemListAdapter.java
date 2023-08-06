package com.gprod.mediaio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gprod.mediaio.R;
import com.gprod.mediaio.enums.home.HomeItemTypes;
import com.gprod.mediaio.interfaces.adapters.HomeItemViewTypes;
import com.gprod.mediaio.models.home.FeedHomeItem;
import com.gprod.mediaio.models.home.HomeItem;
import com.gprod.mediaio.models.home.StoriesHomeItem;
import com.gprod.mediaio.models.story.StoryItem;

import java.util.ArrayList;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.HomeItemHolder> {
    private ArrayList<HomeItem> homeItemList;
    private LayoutInflater inflater;
    public HomeItemListAdapter(Context context, ArrayList<HomeItem> homeItemList){
        this.homeItemList = homeItemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        HomeItem homeItem = homeItemList.get(position);
        if(homeItem.getHomeItemTypes() == HomeItemTypes.STORIES_ITEM){
            return HomeItemViewTypes.STORIES_HOME_ITEM;
        }
        else if(homeItem.getHomeItemTypes() == HomeItemTypes.FEED_ITEM){
            return HomeItemViewTypes.FEED_HOME_ITEM;
        }
        else {
            return HomeItemViewTypes.VIEW_TYPE_NOT_HAVE;
        }
    }

    @NonNull
    @Override
    public HomeItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == HomeItemViewTypes.FEED_HOME_ITEM){
            view = inflater.inflate(R.layout.feed_home_item,parent,false);
            FeedItemHolder feedItemHolder = new FeedItemHolder(view);
            return feedItemHolder;
        }
        else if(viewType == HomeItemViewTypes.STORIES_HOME_ITEM){
            view = inflater.inflate(R.layout.story_home_item,parent,false);
            StoryListItemHolder storyListItemHolder = new StoryListItemHolder(view);
            return storyListItemHolder;
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemHolder holder, int position) {
        if(holder instanceof StoryListItemHolder){
            ((StoryListItemHolder) holder).setData((StoriesHomeItem) homeItemList.get(position));
        }
        else if(holder instanceof FeedItemHolder){
            ((FeedItemHolder) holder).setData((FeedHomeItem) homeItemList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return homeItemList.size();
    }

    abstract class HomeItemHolder extends RecyclerView.ViewHolder {
        public HomeItemHolder(@NonNull View itemView) {super(itemView);}
    }
    class StoryListItemHolder extends HomeItemHolder{
        RecyclerView storyItemListView;
        LinearLayoutManager linearLayoutManager;
        public StoryListItemHolder(@NonNull View itemView) {
            super(itemView);
            storyItemListView = itemView.findViewById(R.id.storyItemListView);
            linearLayoutManager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
        }
        public void setData(StoriesHomeItem storiesHomeItem){
            storyItemListView.setLayoutManager(linearLayoutManager);
            storyItemListView.setAdapter(storiesHomeItem.getStoryItemListAdapter());
        }
    }
    class FeedItemHolder extends HomeItemHolder{
        private RecyclerView postListView;
        public FeedItemHolder(@NonNull View itemView) {
            super(itemView);
            postListView = itemView.findViewById(R.id.postListView);
            postListView.setLayoutManager(new LinearLayoutManager(itemView.getContext(),
                    LinearLayoutManager.VERTICAL,false));
        }
        public void setData(FeedHomeItem feedHomeItem){
            postListView.setAdapter(feedHomeItem.getPostListAdapter());
        }
    }

}
