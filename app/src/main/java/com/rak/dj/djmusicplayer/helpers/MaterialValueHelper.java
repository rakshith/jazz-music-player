package com.rak.dj.djmusicplayer.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;

import com.rak.dj.djmusicplayer.R;

/**
 * Created by sraksh on 2/17/2018.
 */

public class MaterialValueHelper {
    @SuppressLint("PrivateResource")
    @ColorInt
    public static int getPrimaryTextColor(final Context context, boolean dark) {
        if (dark) {
            return ContextCompat.getColor(context, R.color.primary_text_default_material_light);
        }
        return ContextCompat.getColor(context, R.color.primary_text_default_material_dark);
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int getSecondaryTextColor(final Context context, boolean dark) {
        if (dark) {
            return ContextCompat.getColor(context, R.color.secondary_text_default_material_light);
        }
        return ContextCompat.getColor(context, R.color.secondary_text_default_material_dark);
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int getPrimaryDisabledTextColor(final Context context, boolean dark) {
        if (dark) {
            return ContextCompat.getColor(context, R.color.primary_text_disabled_material_light);
        }
        return ContextCompat.getColor(context, R.color.primary_text_disabled_material_dark);
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int getSecondaryDisabledTextColor(final Context context, boolean dark) {
        if (dark) {
            return ContextCompat.getColor(context, R.color.secondary_text_disabled_material_light);
        }
        return ContextCompat.getColor(context, R.color.secondary_text_disabled_material_dark);
    }

    private MaterialValueHelper() {
    }
}
