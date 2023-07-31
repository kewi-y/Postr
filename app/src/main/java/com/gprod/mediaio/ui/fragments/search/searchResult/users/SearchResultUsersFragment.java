package com.gprod.mediaio.ui.fragments.search.searchResult.users;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.SearchResultListAdapter;
import com.gprod.mediaio.enums.search.SearchResultItemsTypes;
import com.gprod.mediaio.interfaces.adapters.SearchResultItemClickListener;
import com.gprod.mediaio.models.search.SearchResultItem;
import com.gprod.mediaio.models.search.SearchResultProfileItem;

import java.util.ArrayList;

public class SearchResultUsersFragment extends Fragment {

    private SearchResultUsersViewModel viewModel;
    private RecyclerView searchResultUserList;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private ArrayList<SearchResultItem> searchResultProfileItemList;

    public SearchResultUsersFragment(ArrayList<SearchResultItem> searchResultProfileItemList){
        this.searchResultProfileItemList = searchResultProfileItemList;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SearchResultUsersViewModel.class);
        View root = inflater.inflate(R.layout.search_result_users_fragment, container, false);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        searchResultUserList = root.findViewById(R.id.searchResultUsersList);
        searchResultUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        SearchResultListAdapter searchResultUserListAdapter = new SearchResultListAdapter(getContext(), searchResultProfileItemList, new SearchResultItemClickListener() {
            @Override
            public void onClick(SearchResultItem searchResultItem) {
                if(searchResultItem.getSearchResultItemType().equals(SearchResultItemsTypes.PROFILE_ITEM)){
                    viewModel.selectUser(((SearchResultProfileItem)searchResultItem).getUser());
                    navController.navigate(R.id.navigation_profile);
                }
            }
        });
        searchResultUserList.setAdapter(searchResultUserListAdapter);
        return root;
    }
}