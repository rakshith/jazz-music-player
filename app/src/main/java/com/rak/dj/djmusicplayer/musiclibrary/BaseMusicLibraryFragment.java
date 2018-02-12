package com.rak.dj.djmusicplayer.musiclibrary;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.widgets.BaseRecyclerView;
import com.rak.dj.djmusicplayer.widgets.FastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseMusicLibraryFragment extends Fragment {

    protected FastScroller fastScroller;
    protected BaseRecyclerView recyclerView;

    public BaseMusicLibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return setBaseListView(inflater, container, savedInstanceState );
    }


    public abstract View setBaseListView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState );



}
