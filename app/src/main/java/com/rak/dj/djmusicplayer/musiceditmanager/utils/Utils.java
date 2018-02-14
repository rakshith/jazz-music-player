package com.rak.dj.djmusicplayer.musiceditmanager.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import static com.rak.dj.djmusicplayer.musiceditmanager.utils.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;
import static com.rak.dj.djmusicplayer.musiceditmanager.utils.Constants.REQUEST_ID_READ_CONTACTS_PERMISSION;
import static com.rak.dj.djmusicplayer.musiceditmanager.utils.Constants.REQUEST_ID_RECORD_AUDIO_PERMISSION;

/**
 * Created by sraksh on 2/14/2018.
 */

public class Utils {


    public static int getDimensionInPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(0, dp, context.getResources().getDisplayMetrics());
    }

    public static boolean checkAndRequestAudioPermissions(Activity activity) {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_ID_RECORD_AUDIO_PERMISSION);
            return false;
        }
        return true;
    }

    public static boolean checkAndRequestContactsPermissions(Activity activity) {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_ID_READ_CONTACTS_PERMISSION);
            return false;
        }
        return true;
    }

    public static boolean checkAndRequestPermissions(Activity activity, boolean ask) {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (ask) {
                ActivityCompat.requestPermissions(activity,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            } else {
                return false;
            }
        }
        return true;
    }
}
