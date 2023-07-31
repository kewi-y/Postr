package com.gprod.mediaio.ui.fragments.search.searchResult.hashtags;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.SearchResultListAdapter;
import com.gprod.mediaio.enums.search.SearchResultItemsTypes;
import com.gprod.mediaio.interfaces.adapters.SearchResultItemClickListener;
import com.gprod.mediaio.models.search.SearchResultItem;
import com.gprod.mediaio.models.search.SearchResultProfileItem;
import com.gprod.mediaio.models.search.SearchResultTagItem;

import java.util.ArrayList;

public class SearchResultHashtagsFragment extends Fragment {

    private SearchResultHashtagsViewModel viewModel;
    private ArrayList<SearchResultItem> searchResultTagItemList;
    private RecyclerView searchResultTagList;
    private NavHostFragment navHostFragment;
    private NavController navController;

    public SearchResultHashtagsFragment(ArrayList<SearchResultItem> searchResultTagItemList){
        this.searchResultTagItemList = searchResultTagItemList;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SearchResultHashtagsViewModel.class);
        View root = inflater.inflate(R.layout.search_result_hashtags_fragment, container, false);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        searchResultTagList = root.findViewById(R.id.searchResultTagsList);
        searchResultTagList.setLayoutManager(new LinearLayoutManager(getContext()));
        SearchResultListAdapter searchResultListAdapter = new SearchResultListAdapter(getContext(), searchResultTagItemList, new SearchResultItemClickListener() {
            @Override
            public void onClick(SearchResultItem searchResultItem) {
                if(searchResultItem.getSearchResultItemType().equals(SearchResultItemsTypes.TAG_ITEM)){
                    viewModel.selectTag(((SearchResultTagItem) searchResultItem).getTag());
                    navController.navigate(R.id.detailedTagFragment);
                }
            }
        });
        searchResultTagList.setAdapter(searchResultListAdapter);
        return root;
    }
}