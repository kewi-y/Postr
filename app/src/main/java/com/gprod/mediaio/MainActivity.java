package com.gprod.mediaio;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.zxing.WriterException;
import com.gprod.mediaio.interfaces.dialogs.navigationAdd.NavigationAddDialogCallback;
import com.gprod.mediaio.repositories.NfcParsedUserRepository;
import com.gprod.mediaio.repositories.QrCodeRepository;
import com.gprod.mediaio.services.nfc.NfcService;
import com.gprod.mediaio.services.popup.loading.LoadingPopup;
import com.gprod.mediaio.services.popup.nfc.NfcSharingPopup;
import com.gprod.mediaio.services.popup.notification.NotificationPopup;
import com.gprod.mediaio.services.popup.progress.ProgressPopup;
import com.gprod.mediaio.ui.dialogs.navigationAdd.NavigationAddDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;


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
        View sharingPopupView = findViewById(R.id.sharingPopupView);
        NotificationPopup.initialize(notificationsTextView);
        LoadingPopup.initialize(loadingPopupView);
        ProgressPopup.initialize(progressPopupView,progressPopupBar);
        NfcSharingPopup.initialize(sharingPopupView);
        TypedValue primaryColorValue = new TypedValue();
        TypedValue backgroundColorValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorPrimary,primaryColorValue,true);
        theme.resolveAttribute(R.attr.colorSecondary,backgroundColorValue,true);
        QrCodeRepository.initialize(primaryColorValue.data,backgroundColorValue.data);
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

                    @Override
                    public void onSelectedQr() {
                        navController.navigate(R.id.scanQrFragment);
                    }
                });
            }
        });

    }
}