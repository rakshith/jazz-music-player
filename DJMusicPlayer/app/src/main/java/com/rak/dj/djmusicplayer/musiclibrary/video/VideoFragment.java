package com.rak.dj.djmusicplayer.musiclibrary.video;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.VideoLoader;
import com.rak.dj.djmusicplayer.musiclibrary.BaseMusicLibraryFragment;
import com.rak.dj.djmusicplayer.musiclibrary.genres.GenresFragment;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;
import com.rak.dj.djmusicplayer.widgets.FastScroller;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends BaseMusicLibraryFragment {

    AlbumVideoAdapter albumVideoAdapter;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View setBaseListView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recylerview, container, false);
        fastScroller = rootView.findViewById(R.id.fastscroller);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FastScroller fastScroller =  rootView.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(recyclerView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new loadAlbumVideos().execute("");
    }


    private class loadAlbumVideos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                albumVideoAdapter = new AlbumVideoAdapter((AppCompatActivity) getActivity(), VideoLoader.getAlbumVideos(getActivity()), false);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(albumVideoAdapter);
            if (getActivity() != null)
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        }

        @Override
        protected void onPreExecute() {
        }
    }
}
