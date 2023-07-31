package com.gprod.mediaio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.adapters.AlbumClickListener;
import com.gprod.mediaio.models.album.Album;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class AlbumPagerItemListAdapter extends RecyclerView.Adapter<AlbumPagerItemListAdapter.AlbumPagerItemHolder> {
    private ArrayList<Album> albumList;
    private LayoutInflater inflater;
    private AlbumClickListener albumClickListener;
    private int albumCountToShow;
    public AlbumPagerItemListAdapter(Context context){
        albumCountToShow = Integer.parseInt(context.getResources().getString(R.string.album_item_count_to_show));
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
    public AlbumPagerItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.album_pager_item,parent,false);
        return new AlbumPagerItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumPagerItemHolder holder, int position) {
        ArrayList<Album> albumListToShow = new ArrayList<>();
        int firstItemIndex,lastItemIndex;
        if(position == 0){
            firstItemIndex = position;
            lastItemIndex = albumCountToShow;
        }
        else {
            firstItemIndex = albumCountToShow * position;
            lastItemIndex = firstItemIndex + albumCountToShow;
        }
        for(int i = firstItemIndex; i < lastItemIndex && i < albumList.size(); i++){
            albumListToShow.add(albumList.get(i));
        }
        holder.setData(albumListToShow);
    }

    @Override
    public int getItemCount() {
        if(albumList != null) {
            int itemCount;
            itemCount = albumList.size()/albumCountToShow;
            if(albumList.size() % albumCountToShow > 0) {
                itemCount += 1;
            }
            return itemCount;
        }
        else {
            return 0;
        }
    }

    class AlbumPagerItemHolder extends RecyclerView.ViewHolder{
        private RecyclerView albumItemList;
        private AlbumItemListAdapter albumItemListAdapter;
        private View itemView;
        public AlbumPagerItemHolder(@NonNull View itemView) {
            super(itemView);
            albumItemList = itemView.findViewById(R.id.albumItemList);
            albumItemListAdapter = new AlbumItemListAdapter(itemView.getContext());
            albumItemList.setLayoutManager(new GridLayoutManager(itemView.getContext(),3));
            this.itemView = itemView;
        }
        public void setData(ArrayList<Album> albums){
            if(albumClickListener != null){
                albumItemListAdapter.setAlbumClickListener(albumClickListener);
            }
            albumItemListAdapter.updateAlbumList(albums);
            albumItemList.setAdapter(albumItemListAdapter);
        }
        public Context getContext(){
            return itemView.getContext();
        }
    }
}
