package com.gprod.mediaio;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.gprod.mediaio.interfaces.dialogs.navigationAdd.NavigationAddDialogCallback;
import com.gprod.mediaio.interfaces.services.story.AddingStoryCallback;
import com.gprod.mediaio.models.story.ImageStory;
import com.gprod.mediaio.repositories.StoryRepository;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;
import com.gprod.mediaio.services.popup.progress.ProgressPopup;
import com.gprod.mediaio.ui.dialogs.navigationAdd.NavigationAddDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


import java.sql.Timestamp;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private NavigationAddDialog navigationAddDialog;
    private BottomNavigationItemView bottomNavigationAddItemView;
    private BottomNavigationView navView;
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        TextView notificationsTextView = findViewById(R.id.notificationTextView);
        ProgressBar loadingPopupView = findViewById(R.id.loadingView);
        ProgressBar progressPopupBar = findViewById(R.id.progressPopupBar);
        View progressPopupView = findViewById(R.id.progressPopup);
        NotificationPopup.initialize(notificationsTextView);
        LoadingPopup.initialize(loadingPopupView);
        ProgressPopup.initialize(progressPopupView,progressPopupBar);
        navView = findViewById(R.id.nav_view);
        bottomNavigationAddItemView = navView.findViewById(R.id.navigation_add);
        navView.setVisibility(View.GONE);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView,navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationAddDialog.init(this,bottomNavigationAddItemView);
        navigationAddDialog = NavigationAddDialog.getInstance();
        bottomNavigationAddItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationAddDialog.show(new NavigationAddDialogCallback() {
                    @Override
                    public void onSelectPost() {
                        navView.setVisibility(View.GONE);
                        navController.navigate(R.id.add_image_post_fragment);
                    }

                    @Override
                    public void onSelectStory() {
                        navView.setVisibility(View.GONE);
                        navController.navigate(R.id.addStoryFragment);
                    }
                });
            }
        });
    }
}