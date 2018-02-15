package com.rak.dj.djmusicplayer.musiclibrary.genres;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.GenreLoader;
import com.rak.dj.djmusicplayer.musiclibrary.BaseMusicLibraryFragment;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;
import com.rak.dj.djmusicplayer.widgets.FastScroller;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends BaseMusicLibraryFragment implements MusicStateListener {


    private GenreAdapter genreAdapter;

    public GenresFragment() {
        // Required empty public constructor
    }

    @Override
    public View setBaseListView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((BaseMainActivity) getActivity()).setMusicStateListenerListener(this);
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

        new loadGenres().execute("");
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        if (genreAdapter != null) {
            genreAdapter.notifyDataSetChanged();
        }
    }

    private class loadGenres extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                genreAdapter = new GenreAdapter((AppCompatActivity) getActivity(), GenreLoader.getAllGenres(getActivity()), false);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(genreAdapter);
            if (getActivity() != null)
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        }

        @Override
        protected void onPreExecute() {
        }
    }
}
