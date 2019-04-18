package com.rak.dj.djmusicplayer.playingmanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.afollestad.appthemeengine.customizers.ATEStatusBarCustomizer;
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer;
import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;

public class NowPlayingActivity extends BaseMainActivity implements ATEActivityThemeCustomizer, ATEToolbarCustomizer, ATEStatusBarCustomizer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE);
        String fragmentID = prefs.getString(Constants.NOWPLAYING_FRAGMENT_ID, Constants.JAZZ4);

        Fragment fragment = NavigationUtil.getFragmentForNowplayingID(fragmentID);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();

    }

    @Override
    public View createContentView() {
        View nowPlayingView = getLayoutInflater().inflate(R.layout.activity_now_playing, null);
        return nowPlayingView;
    }

    @StyleRes
    @Override
    public int getActivityTheme() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ? R.style.AppTheme_FullScreen_Dark : R.style.AppTheme_FullScreen_Light;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferencesUtils.getInstance(this).didNowplayingThemeChanged()) {
            PreferencesUtils.getInstance(this).setNowPlayingThemeChanged(false);
            recreate();
        }
    }

    @Override
    protected void loadOnPermissionGranted() {

    }

    @Override
    public int getLightStatusBarMode() {
        return Config.LIGHT_STATUS_BAR_OFF;
    }

    @Override
    public int getStatusBarColor() {
        return Color.TRANSPARENT;
    }


    @Override
    public int getLightToolbarMode() {
        return Config.LIGHT_STATUS_BAR_OFF;
    }

    @Override
    public int getToolbarColor() {
        return  Color.TRANSPARENT;
    }
}
