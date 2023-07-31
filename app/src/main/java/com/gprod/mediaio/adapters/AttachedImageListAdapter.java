package com.gprod.mediaio.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gprod.mediaio.R;

import java.util.ArrayList;

public class AttachedImageListAdapter extends RecyclerView.Adapter<AttachedImageListAdapter.AttachedImageHolder> {
    private ArrayList<Bitmap> attachedImageList;
    private LayoutInflater inflater;

    public AttachedImageListAdapter(Context context, ArrayList<Bitmap> attachedImageList){
        this.attachedImageList = attachedImageList;
        inflater = LayoutInflater.from(context);
    }
    public void updateAttachedImageList(ArrayList<Bitmap> attachedImageList){
        this.attachedImageList = attachedImageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttachedImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.attached_image_item,parent,false);
        return new AttachedImageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachedImageHolder holder, int position) {
        holder.setData(attachedImageList.get(position));
    }


    @Override
    public int getItemCount() {
        return attachedImageList.size();
    }
    class AttachedImageHolder extends RecyclerView.ViewHolder{
        ImageView attachedImagePreviewView;
        public AttachedImageHolder(@NonNull View itemView) {
            super(itemView);
            attachedImagePreviewView = itemView.findViewById(R.id.attachedImagePreviewView);
        }
        public void setData(Bitmap image){
            attachedImagePreviewView.setImageBitmap(image);
        }
    }
}
