package com.gprod.mediaio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.home.StoryItemClickListener;
import com.gprod.mediaio.models.story.StoryItem;

import java.util.ArrayList;

public class StoryItemListAdapter extends RecyclerView.Adapter<StoryItemListAdapter.StoryItemHolder> {
    private ArrayList<StoryItem> storyItems;
    private LayoutInflater inflater;
    private StoryItemClickListener storyItemClickListener;
    public StoryItemListAdapter(Context context, StoryItemClickListener storyItemClickListener){
        inflater = LayoutInflater.from(context);
        this.storyItemClickListener = storyItemClickListener;
    }
    public void updateStoryItems(ArrayList<StoryItem> storyItems){
        this.storyItems = storyItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View storyItemView = inflater.inflate(R.layout.story_item,parent,false);
        StoryItemHolder storyItemHolder = new StoryItemHolder(storyItemView);
        return storyItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryItemHolder holder, int position) {
        holder.setData(storyItems.get(position),storyItemClickListener);
    }

    @Override
    public int getItemCount() {
        if(storyItems != null) {
            return storyItems.size();
        }
        else {
            return 0;
        }
    }

    class StoryItemHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView storyItemProfilePhotoView;
        TextView storyItemAccountnameView;
        View itemView;
        public StoryItemHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            storyItemProfilePhotoView = itemView.findViewById(R.id.storyItemProfilePhotoView);
            storyItemAccountnameView = itemView.findViewById(R.id.storyItemAccountnameView);
        }
        public void setData(StoryItem storyItem, StoryItemClickListener storyItemClickListener){
            storyItemAccountnameView.setText(storyItem.getAccountname());
            storyItemProfilePhotoView.setImageURI(storyItem.getUserProfilePhotoDownloadUri());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storyItemClickListener.onClick(storyItem);
                }
            });
        }
    }
}
