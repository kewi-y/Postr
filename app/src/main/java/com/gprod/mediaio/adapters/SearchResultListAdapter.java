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
import com.gprod.mediaio.enums.search.SearchResultItemsTypes;
import com.gprod.mediaio.interfaces.adapters.SearchResultItemClickListener;
import com.gprod.mediaio.interfaces.adapters.SearchResultItemViewTypes;
import com.gprod.mediaio.models.search.SearchResultItem;
import com.gprod.mediaio.models.search.SearchResultProfileItem;
import com.gprod.mediaio.models.search.SearchResultTagItem;

import java.util.ArrayList;

public class SearchResultListAdapter extends RecyclerView.Adapter<SearchResultListAdapter.SearchResultItemHolder> {
    private SearchResultItemClickListener clickListener;
    private ArrayList<SearchResultItem> searchResultItemList;
    private LayoutInflater inflater;
    public SearchResultListAdapter(Context context, ArrayList<SearchResultItem> searchResultItemList, SearchResultItemClickListener searchResultItemClickListener){
        inflater = LayoutInflater.from(context);
        this.clickListener = searchResultItemClickListener;
        this.searchResultItemList = searchResultItemList;
    }
    public void updateSearchResultItemList(ArrayList<SearchResultItem> searchResultItemList){
        this.searchResultItemList = searchResultItemList;
    }

    @Override
    public int getItemViewType(int position) {
        SearchResultItem searchResultItem = searchResultItemList.get(position);
        if(searchResultItem.getSearchResultItemType() == SearchResultItemsTypes.PROFILE_ITEM){
            return SearchResultItemViewTypes.PROFILE_ITEM_VIEW_TYPE;
        }
        else if (searchResultItem.getSearchResultItemType() == SearchResultItemsTypes.TAG_ITEM){
            return SearchResultItemViewTypes.TAG_ITEM_VIEW_TYPE;
        }
        else {
            return SearchResultItemViewTypes.VIEW_TYPE_NOT_HAVE;
        }
    }

    @NonNull
    @Override
    public SearchResultItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == SearchResultItemViewTypes.PROFILE_ITEM_VIEW_TYPE){
            view = inflater.inflate(R.layout.search_result_profile_item,parent,false);
            SearchResultProfileItemHolder searchResultProfileItemHolder = new SearchResultProfileItemHolder(view);
            return searchResultProfileItemHolder;
        }
        if(viewType == SearchResultItemViewTypes.TAG_ITEM_VIEW_TYPE){
            view = inflater.inflate(R.layout.search_result_tag_item,parent,false);
            SearchResultTagItemHolder searchResultTagItemHolder = new SearchResultTagItemHolder(view);
            return searchResultTagItemHolder;
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultItemHolder holder, int position) {
        holder.setClickListener(clickListener);
        holder.setData(searchResultItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return searchResultItemList.size();
    }

    abstract class SearchResultItemHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private SearchResultItemClickListener clickListener;
        public SearchResultItemHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
        public void setClickListener(SearchResultItemClickListener clickListener){
            this.clickListener = clickListener;
        }
        public void setData(SearchResultItem searchResultItem){
            if(itemView != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.onClick(searchResultItem);
                    }
                });
            }
        }
    }

    class SearchResultProfileItemHolder extends SearchResultItemHolder {
        private SimpleDraweeView profilePhotoView;
        private TextView nameView,profilenameView;
        private View itemView;
        public SearchResultProfileItemHolder(@NonNull View itemView) {
            super(itemView);
            profilePhotoView = itemView.findViewById(R.id.searchResultItemProfilePhotoView);
            nameView = itemView.findViewById(R.id.searchResultProfileItemUsernameView);
            profilenameView = itemView.findViewById(R.id.searchResultProfileItemProfilenameView);
            this.itemView = itemView;
        }
        @Override
        public void setData(SearchResultItem searchResultItem) {
            super.setData(searchResultItem);
            profilePhotoView.setImageURI(((SearchResultProfileItem)searchResultItem).getUser().getProfilePhotoDownloadUri());
            nameView.setText(((SearchResultProfileItem)searchResultItem).getUser().getUsername());
            profilenameView.setText(((SearchResultProfileItem)searchResultItem).getUser().getProfilename());
        }
    }
    class SearchResultTagItemHolder extends SearchResultItemHolder {
        private TextView tagName,viewCount;
        public SearchResultTagItemHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.searchResultTagItemNameView);
            viewCount = itemView.findViewById(R.id.searchResultTagItemCountView);
        }

        @Override
        public void setData(SearchResultItem searchResultItem) {
            super.setData(searchResultItem);
            Long longViewCount = ((SearchResultTagItem)searchResultItem).getTag().getViewCount();
            tagName.setText(((SearchResultTagItem)searchResultItem).getTag().getStringTag());
            viewCount.setText(String.valueOf(longViewCount));
        }
    }
}
