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
import com.gprod.mediaio.interfaces.adapters.AlbumClickListener;
import com.gprod.mediaio.models.album.Album;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AlbumItemListAdapter extends RecyclerView.Adapter<AlbumItemListAdapter.AlbumItemHolder> {
    private ArrayList<Album> albumList;
    private LayoutInflater inflater;
    private AlbumClickListener albumClickListener;
    public AlbumItemListAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }
    public void updateAlbumList(ArrayList<Album> albumList){
        this.albumList = albumList;
        notifyDataSetChanged();
    }
    public void setAlbumClickListener(AlbumClickListener albumClickListener){
        this.albumClickListener = albumClickListener;
    }
    @NonNull
    @Override
    public AlbumItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.album_item,parent,false);
        return new AlbumItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumItemHolder holder, int position) {
        if(albumList != null){
            holder.setData(albumList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(albumList != null){
            return albumList.size();
        }
        else {
            return 0;
        }
    }

    class AlbumItemHolder extends RecyclerView.ViewHolder{
        View itemView;
        SimpleDraweeView albumPreviewView;
        TextView albumNameView;
        public AlbumItemHolder(@NonNull View itemView) {
            super(itemView);
            albumPreviewView = itemView.findViewById(R.id.albumPreviewView);
            albumNameView = itemView.findViewById(R.id.albumNameView);
            this.itemView = itemView;
        }
        public void setData(Album album){
            albumPreviewView.setImageURI(album.getPreviewImageDownloadUri());
            albumNameView.setText(album.getName());
            if(albumClickListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        albumClickListener.onClick(album);
                    }
                });
            }
        }
    }
}
