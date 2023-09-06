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
import com.gprod.mediaio.interfaces.adapters.DetachImageListener;

import java.util.ArrayList;

public class AttachedImageListAdapter extends RecyclerView.Adapter<AttachedImageListAdapter.AttachedImageHolder> {
    private ArrayList<Bitmap> attachedImageList;
    private LayoutInflater inflater;
    private DetachImageListener detachImageListener;

    public AttachedImageListAdapter(Context context, ArrayList<Bitmap> attachedImageList){
        this.attachedImageList = attachedImageList;
        inflater = LayoutInflater.from(context);
    }
    public void updateAttachedImageList(ArrayList<Bitmap> attachedImageList){
        this.attachedImageList = attachedImageList;
        notifyDataSetChanged();
    }
    public void setDetachImageListener(DetachImageListener detachImageListener){
        this.detachImageListener = detachImageListener;
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
        ImageView attachedImagePreviewView, detachImageView;
        public AttachedImageHolder(@NonNull View itemView) {
            super(itemView);
            attachedImagePreviewView = itemView.findViewById(R.id.attachedImagePreviewView);
            detachImageView = itemView.findViewById(R.id.detachImageImageView);
        }
        public void setData(Bitmap image){
            attachedImagePreviewView.setImageBitmap(image);
            if(detachImageListener != null){
                detachImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detachImageListener.onDetach(image);
                    }
                });
            }
        }
    }
}
