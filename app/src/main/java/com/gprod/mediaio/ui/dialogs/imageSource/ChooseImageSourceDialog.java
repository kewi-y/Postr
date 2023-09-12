package com.gprod.mediaio.ui.dialogs.imageSource;

import android.app.Activity;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.dialogs.imageSourceDialog.ChooseImageSourceDialogCallback;

public class ChooseImageSourceDialog {
    private static ChooseImageSourceDialog instance;
    private View dialogSourceGalleryItem,dialogSourceCameraItem;
    private BottomSheetDialog dialog;
    public static ChooseImageSourceDialog getInstance(Activity activity){
        if(instance == null){
            instance = new ChooseImageSourceDialog(activity);
        }
        return instance;
    }
    private ChooseImageSourceDialog(Activity activity){
        dialog = new BottomSheetDialog(activity);
        dialog.setContentView(R.layout.choose_image_source_dialog);
        dialogSourceCameraItem = dialog.findViewById(R.id.dialogCameraSourceItem);
        dialogSourceGalleryItem = dialog.findViewById(R.id.dialogGallerySourceItem);
    }
    public void show(ChooseImageSourceDialogCallback chooseImageSourceDialogCallback){
        instance.dialogSourceGalleryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageSourceDialogCallback.onChooseGallery();
                instance.dialog.dismiss();
            }
        });
        instance.dialogSourceCameraItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageSourceDialogCallback.onChooseCamera();
                instance.dialog.dismiss();
            }
        });
        instance.dialog.show();
    }
}
