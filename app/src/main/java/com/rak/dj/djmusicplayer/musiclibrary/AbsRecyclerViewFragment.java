package com.rak.dj.djmusicplayer.musiclibrary;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.ThemeStore;
import com.rak.dj.djmusicplayer.helpers.ViewUtil;
import com.rak.dj.djmusicplayer.widgets.BaseRecyclerView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AbsRecyclerViewFragment extends Fragment {


    protected BaseRecyclerView recyclerView;

    public AbsRecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setBaseListView(inflater, container, savedInstanceState );

        if(view != null){
            recyclerView = view.findViewById(R.id.recyclerView);
            ViewUtil.setUpFastScrollRecyclerViewColor(getActivity(), ((FastScrollRecyclerView) recyclerView), ThemeStore.accentColor(getActivity()));
        }
        return view;
    }


    public abstract View setBaseListView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState );


}
