package com.rak.dj.djmusicplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer;
import com.rak.dj.djmusicplayer.musiceditmanager.AbsPermissionsActivity;

/**
 * Created by sraksh on 2/15/2018.
 */

public abstract class BaseThemedActivity extends AbsPermissionsActivity implements ATEActivityThemeCustomizer {


    protected boolean isDarkTheme;

    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeNormalDark : R.style.AppThemeNormalLight;
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);
        setAppTheme();
    }

    protected void setAppTheme(){
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAppTheme();
    }

    @Override
    protected void loadOnPermissionGranted() {

    }
}
