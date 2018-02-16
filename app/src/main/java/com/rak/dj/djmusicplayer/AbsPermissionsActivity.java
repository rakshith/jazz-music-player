package com.rak.dj.djmusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;

import com.rak.dj.djmusicplayer.permissions.JazzPermissionManger;
import com.rak.dj.djmusicplayer.permissions.PermissionCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import static com.rak.dj.djmusicplayer.helpers.Constants.ACTION_WRITE_SETTINGS_PERMISSION;

/**
 * Created by sraksh on 2/14/2018.
 */

public abstract class AbsPermissionsActivity extends BaseThemedActivity{

    protected SlidingUpPanelLayout slidingUpPanelLayout;

    private final PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadOnPermissionGranted();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };

    protected void checkExternalStoragePermissionAndThenLoad() {
        //check for permission
        if (JazzPermissionManger.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            loadOnPermissionGranted();
        } else {
            if (JazzPermissionManger.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(slidingUpPanelLayout, "Jazz Music Player will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", view -> JazzPermissionManger.askForPermission(AbsPermissionsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionCallback)).show();
            } else {
                JazzPermissionManger.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionCallback);
            }
        }
    }

    protected void checkWriteSettingsPermissionAndThenLoad(Context context) {
        boolean settingsCanWrite = Settings.System.canWrite(context);
        //check for permission
        if (settingsCanWrite) {
            loadOnPermissionGranted();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivityForResult(intent, ACTION_WRITE_SETTINGS_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case ACTION_WRITE_SETTINGS_PERMISSION:
                    loadOnPermissionGranted();
                break;
        }
    }

    protected abstract void loadOnPermissionGranted();
}
