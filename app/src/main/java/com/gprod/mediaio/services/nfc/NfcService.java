package com.gprod.mediaio.services.nfc;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.MimeTypeFilter;

import com.gprod.mediaio.R;
import com.gprod.mediaio.interfaces.services.nfc.ShareNfcCallback;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NfcService {
    private static NfcService instance;
    private NfcAdapter nfcAdapter;
    private String sharingTag;

    public static NfcService getInstance() {
        return instance;
    }
    public static void initialize(Context context ,NfcAdapter nfcAdapter){
        instance = new NfcService(context,nfcAdapter);
    }
    private NfcService(Context context, NfcAdapter nfcAdapter){
        this.nfcAdapter = nfcAdapter;
        this.sharingTag = context.getResources().getString(R.string.nfc_sharing_tag);
    }
    public String readUserFromIntent(NdefMessage ndefMessage){
        String message = null;
        if(ndefMessage.getRecords().length > 0){
            NdefRecord[] ndefRecords = ndefMessage.getRecords();
            if(ndefRecords[0].toMimeType().equals("text/plain") && ndefRecords.length == 2){
                String tag = new String(ndefRecords[0].getPayload(),0,ndefRecords[0].getPayload().length,StandardCharsets.UTF_8);
                if(tag.contains(sharingTag)){
                    byte[] userIdByteArray = Arrays.copyOfRange(ndefRecords[1].getPayload(),3,ndefRecords[1].getPayload().length);
                    String userId = new String(userIdByteArray,0,userIdByteArray.length,StandardCharsets.UTF_8);
                    message = userId;
                    Log.d("MY LOGS","User id from tag >>: " + message);
                }
            }
        }
        return message;
    }
    public void shareNfcUserID(Activity activity, String userId, ShareNfcCallback shareNfcCallback){
        nfcAdapter.disableReaderMode(activity);
        nfcAdapter.setOnNdefPushCompleteCallback(new NfcAdapter.OnNdefPushCompleteCallback() {
            @Override
            public void onNdefPushComplete(NfcEvent nfcEvent) {
                shareNfcCallback.onShared();
            }
        }, activity);
        NdefRecord[] ndefRecords = {NdefRecord.createTextRecord("en",sharingTag),
                NdefRecord.createTextRecord("en",userId)};
        NdefMessage ndefMessage = new NdefMessage(ndefRecords);
        nfcAdapter.setNdefPushMessage(ndefMessage,activity);
        nfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
            @Override
            public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
                return ndefMessage;
            }
        },activity);
    }
    public void resetSharing(Activity activity){

    }
}
