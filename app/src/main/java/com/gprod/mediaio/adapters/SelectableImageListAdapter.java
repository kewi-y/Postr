package com.gprod.mediaio.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.adapters.OnSelectImageListener;

import java.util.ArrayList;

public class SelectableImageListAdapter extends RecyclerView.Adapter<SelectableImageListAdapter.SelectableImageHolder> {
    private ArrayList<String> imageUriList;
    private LayoutInflater inflater;
    private OnSelectImageListener onSelectImageListener;
    public SelectableImageListAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }
    public void updateImageList(ArrayList<String> imageUriList){
        this.imageUriList = imageUriList;
    }
    public void setOnSelectImageListener(OnSelectImageListener onSelectImageListener){
        this.onSelectImageListener = onSelectImageListener;
    }
    @NonNull
    @Override
    public SelectableImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.selectable_image_item,parent,false);
        SelectableImageHolder selectableImageHolder = new SelectableImageHolder(view);
        return selectableImageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableImageHolder holder, int position) {
        holder.setData(imageUriList.get(position));
    }

    @Override
    public int getItemCount() {
        if(imageUriList != null){
            return imageUriList.size();
        }
        else {
            return 0;
        }
    }

    class SelectableImageHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView selectableImageView;
        private View itemView;
        public SelectableImageHolder(@NonNull View itemView) {
            super(itemView);
            selectableImageView = itemView.findViewById(R.id.selectableImageView);
            this.itemView = itemView;
        }
        public void setData(String imageUri){
            selectableImageView.setImageURI(imageUri);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onSelectImageListener != null){
                        onSelectImageListener.onSelect(imageUri);
                    }
                }
            });
        }
    }
}
