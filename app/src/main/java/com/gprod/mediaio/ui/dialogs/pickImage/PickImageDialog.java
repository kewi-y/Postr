package com.gprod.mediaio.ui.dialogs.pickImage;

import android.app.Dialog;
import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gprod.mediaio.R;
import com.gprod.mediaio.adapters.SelectableImageListAdapter;
import com.gprod.mediaio.interfaces.adapters.OnSelectImageListener;

import java.util.ArrayList;

public class PickImageDialog {
    private static PickImageDialog instance;
    private SelectableImageListAdapter selectableImageListAdapter;
    private RecyclerView galleryImageListView;
    private OnSelectImageListener onSelectImageListener;
    BottomSheetDialog dialog;

    public static PickImageDialog getInstance(Context context) {
        if(instance == null){
            instance = new PickImageDialog(context);
        }
        return instance;
    }
    private PickImageDialog(Context context){
        dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.pick_image_dialog);
        selectableImageListAdapter = new SelectableImageListAdapter(context);
        galleryImageListView = dialog.findViewById(R.id.galleryImageListView);
        galleryImageListView.setLayoutManager(new GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false));
        galleryImageListView.setAdapter(selectableImageListAdapter);

    }
    public void setOnSelectImageListener(OnSelectImageListener onSelectImageListener){
        this.onSelectImageListener = onSelectImageListener;
    }
    public void setImageList(ArrayList<String> imageUriList){
        selectableImageListAdapter.updateImageList(imageUriList);
        selectableImageListAdapter.notifyDataSetChanged();
    }
    public void show(){
        selectableImageListAdapter.setOnSelectImageListener(new OnSelectImageListener() {
            @Override
            public void onSelect(String imageUri) {
                if(onSelectImageListener != null) {
                    onSelectImageListener.onSelect(imageUri);
                }
                dialog.hide();
            }
        });
        dialog.show();
    }

}
