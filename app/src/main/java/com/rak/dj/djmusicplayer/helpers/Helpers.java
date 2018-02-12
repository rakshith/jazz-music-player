package com.rak.dj.djmusicplayer.helpers;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by sraksh on 1/16/2018.
 */

public class Helpers {
    public static String getATEKey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
    }
}
