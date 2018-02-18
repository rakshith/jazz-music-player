package com.rak.dj.djmusicplayer.helpers;

import android.content.Context;

import com.rak.dj.djmusicplayer.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/**
 * Created by sraksh on 2/17/2018.
 */

public class ViewUtil {

    public static void setUpFastScrollRecyclerViewColor(Context context, FastScrollRecyclerView recyclerView, int accentColor) {
        recyclerView.setPopupBgColor(accentColor);
        recyclerView.setPopupTextColor(MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(accentColor)));
        recyclerView.setThumbColor(accentColor);
        recyclerView.setTrackColor(ColorUtil.withAlpha(ATEUtils.resolveColor(context, R.attr.colorControlNormal), 0.12f));
    }
}
