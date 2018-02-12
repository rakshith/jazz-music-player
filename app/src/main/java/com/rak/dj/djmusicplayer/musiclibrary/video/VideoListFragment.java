package com.rak.dj.djmusicplayer.musiclibrary.video;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.appthemeengine.ATE;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.VideoLoader;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.musiclibrary.BaseMusicLibraryFragment;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;
import com.rak.dj.djmusicplayer.widgets.FastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoListFragment extends BaseMusicLibraryFragment {

    String albumName = null;

    VideoListAdapter videoListAdapter;
    private ProgressBar mProgressBar;

    public static VideoListFragment newInstance(String  albumName, boolean useTransition, String transitionName) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ALBUM_VIDEO_ID, albumName);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View setBaseListView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);
        if (getArguments() != null) {
            albumName = getArguments().getString(Constants.ALBUM_VIDEO_ID);
        }
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(albumName);

        fastScroller = rootView.findViewById(R.id.fastscroller);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FastScroller fastScroller =  rootView.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(recyclerView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }

        new loadVideos().execute("");
    }


    private class loadVideos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                videoListAdapter = new VideoListAdapter((AppCompatActivity) getActivity(), VideoLoader.getVideos(getActivity(), albumName), false);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(videoListAdapter);
            if (getActivity() != null)
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

            mProgressBar.setVisibility(View.GONE);
            fastScroller.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPreExecute() {
        }
    }

}
