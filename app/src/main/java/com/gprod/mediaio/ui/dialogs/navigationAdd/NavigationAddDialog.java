package com.gprod.mediaio.ui.dialogs.navigationAdd;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.core.view.GravityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.dialogs.navigationAdd.NavigationAddDialogCallback;

public class NavigationAddDialog {
    private static NavigationAddDialog instance;
    private BottomSheetDialog dialog;
    private PopupMenu popupMenu;
    public static NavigationAddDialog getInstance(){
        return instance;
    }
    public static void init(Context context, View anchor){
        instance = new NavigationAddDialog(context,anchor);
    }
    private NavigationAddDialog(Context context, View anchor){
        popupMenu = new PopupMenu(context, anchor);
        popupMenu.inflate(R.menu.navigation_add_menu);
    }
    public void show(NavigationAddDialogCallback navigationAddDialogCallback){
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.addPostMenuItem:
                        navigationAddDialogCallback.onSelectPost();
                        return true;
                    case R.id.addStoryMenuItem:
                        navigationAddDialogCallback.onSelectStory();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

}
