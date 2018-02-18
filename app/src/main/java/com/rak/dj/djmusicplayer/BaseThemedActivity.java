package com.rak.dj.djmusicplayer;

import android.support.annotation.Nullable;


import com.afollestad.appthemeengine.ATEActivity;
import com.rak.dj.djmusicplayer.helpers.Helpers;

/**
 * Created by sraksh on 2/15/2018.
 */

public abstract class BaseThemedActivity extends ATEActivity {



    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }
}
