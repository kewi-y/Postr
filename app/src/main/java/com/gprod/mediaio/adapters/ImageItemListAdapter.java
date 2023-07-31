package com.gprod.mediaio.adapters;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;

import java.util.ArrayList;

public class ImageItemListAdapter extends RecyclerView.Adapter<ImageItemListAdapter.ImageItemHolder>{
    private ArrayList<String> imageDownloadUriList;
    private LayoutInflater inflater;

    public ImageItemListAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }
    public void setImageDownloadUriList(ArrayList<String> imageDownloadUriList){
        this.imageDownloadUriList = imageDownloadUriList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ImageItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.image_item,parent,false);
        return new ImageItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemHolder holder, int position) {
        holder.setData(imageDownloadUriList.get(position));
    }

    @Override
    public int getItemCount() {
        if(imageDownloadUriList != null) {
            return imageDownloadUriList.size();
        }
        else {
            return 0;
        }
    }

    class ImageItemHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView imageItemView;
        public ImageItemHolder(@NonNull View itemView) {
            super(itemView);
            imageItemView = itemView.findViewById(R.id.imageItemView);
        }
        public void setData(String uri){
            imageItemView.setImageURI(uri);
        }
    }

}
