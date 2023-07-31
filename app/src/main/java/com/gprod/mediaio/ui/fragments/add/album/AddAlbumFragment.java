package com.gprod.mediaio.ui.fragments.add.album;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.SelectablePostListAdapter;
import com.gprod.mediaio.interfaces.repositories.user.AddAlbumCallback;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;

import java.util.ArrayList;

public class AddAlbumFragment extends Fragment {

    private AddAlbumViewModel viewModel;
    private LiveData<ArrayList<Post>> favoritesPostListLiveData;
    private RecyclerView addingPostList;
    private SelectablePostListAdapter selectablePostListAdapter;
    private EditText editAlbumName;
    private ImageButton addAlbumConfirmButton;
    private NavHostFragment navHostFragment;
    private NavController navController;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_album_fragment, container, false);
        viewModel = new ViewModelProvider(this).get(AddAlbumViewModel.class);
        favoritesPostListLiveData = viewModel.getFavoritesPostListLiveData();
        addingPostList = root.findViewById(R.id.addingPostList);
        editAlbumName = root.findViewById(R.id.editAlbumNameView);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        selectablePostListAdapter = new SelectablePostListAdapter(getContext());
        addAlbumConfirmButton = root.findViewById(R.id.addAlbumConfirmButton);
        favoritesPostListLiveData = viewModel.getFavoritesPostListLiveData();
        addingPostList.setLayoutManager(new GridLayoutManager(getContext(),3));
        addingPostList.setAdapter(selectablePostListAdapter);
        favoritesPostListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Post>>() {
            @Override
            public void onChanged(ArrayList<Post> posts) {
                selectablePostListAdapter.updatePostList(posts);
            }
        });
        addAlbumConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editAlbumName.getText().toString().isEmpty()){
                    if(selectablePostListAdapter.getSelectedPostList().size() != 0){
                        viewModel.addAlbum(selectablePostListAdapter.getSelectedPostList(),
                                editAlbumName.getText().toString(), new AddAlbumCallback() {
                                    @Override
                                    public void onSuccess(String albumId) {
                                        navController.navigate(R.id.navigation_favorites);
                                    }

                                    @Override
                                    public void onFailure() {
                                        navController.navigate(R.id.navigation_favorites);
                                    }
                                });
                    }
                    else {
                        NotificationPopup.show(getContext(),false,"Выберите хотя бы один пост");
                    }
                }
                else {
                    NotificationPopup.show(getContext(),false,"Введите имя нового альбома");
                }
            }
        });
        viewModel.loadData(getContext());
        return root;
    }
}