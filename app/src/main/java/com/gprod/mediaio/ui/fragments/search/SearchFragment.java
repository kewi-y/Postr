package com.gprod.mediaio.ui.fragments.search;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.SearchResultFragmentAdapter;
import com.gprod.mediaio.adapters.SearchResultListAdapter;
import com.gprod.mediaio.interfaces.adapters.SearchResultItemClickListener;
import com.gprod.mediaio.models.search.SearchResultItem;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private ImageButton searchButton;
    private EditText searchBarEditText;
    private LiveData<ArrayList<SearchResultItem>>searchResultItemsListLiveData;
    private SearchResultFragmentAdapter searchResultFragmentAdapter;
    private ViewPager2 searchResultFragmentPager;
    private TabLayout searchResultTabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("MY LOGS", "On create search fragment");
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.search_fragment,container,false);
        Animator searchResultTabAnimation = AnimatorInflater.loadAnimator(getContext(),R.animator.show_up);
        Animator searchResultFragmentPagerAnimation = AnimatorInflater.loadAnimator(getContext(),R.animator.show_up);
        searchResultTabAnimation.setTarget(searchResultTabLayout);
        searchResultFragmentPagerAnimation.setTarget(searchResultFragmentPager);
        searchButton = root.findViewById(R.id.searchButton);
        searchBarEditText = root.findViewById(R.id.searchBarEditText);
        searchResultTabLayout = root.findViewById(R.id.searchResultTabLayout);
        searchResultFragmentPager = root.findViewById(R.id.searchResultFragmentPager);
        searchResultItemsListLiveData = viewModel.getSearchResultItemListLiveData();
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(searchResultTabLayout, searchResultFragmentPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0) {
                    tab.setText("Пользователи");
                }
                else {
                    tab.setText("Хэштеги");
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!searchBarEditText.getText().toString().isEmpty()){
                    viewModel.search(getContext(),searchBarEditText.getText().toString());
                    LoadingPopup.show(getContext());
                }
            }
        });
        searchResultItemsListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<SearchResultItem>>() {
            @Override
            public void onChanged(ArrayList<SearchResultItem> searchResultItems) {
                searchResultFragmentAdapter = new SearchResultFragmentAdapter(SearchFragment.this, searchResultItems);
                searchResultFragmentPager.setAdapter(searchResultFragmentAdapter);
                if(!tabLayoutMediator.isAttached()) {
                    tabLayoutMediator.attach();
                }
                searchResultFragmentPager.setVisibility(View.VISIBLE);
                searchResultTabLayout.setVisibility(View.VISIBLE);
                searchResultFragmentPagerAnimation.start();
                searchResultTabAnimation.start();
                LoadingPopup.hide(getContext());
            }
        });
        return root;
    }
}