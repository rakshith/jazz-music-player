package com.rak.dj.djmusicplayer.musiclibrary.minitracks;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.musiclibrary.BaseMusicLibraryFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiniTracksFragment extends BaseMusicLibraryFragment {


    public MiniTracksFragment() {
        // Required empty public constructor
    }

    @Override
    public View setBaseListView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mini_tracks, container, false);
    }

}
