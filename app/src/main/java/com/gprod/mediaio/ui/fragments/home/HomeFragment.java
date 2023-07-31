package com.gprod.mediaio.ui.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.story.AddingStoryCallback;
import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.repositories.StoryRepository;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.home_fragment,container,false);
        return root;
    }
}