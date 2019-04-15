package com.rak.dj.djmusicplayer.musiclibrary.minitracks;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.SongLoader;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiniTracksFragment extends Fragment implements MusicStateListener {

    private FastScrollRecyclerView recyclerView;
    private MiniTracksAdapter miniTracksAdapter;

    public MiniTracksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((BaseMainActivity) getActivity()).setMusicStateListener(this);
        View rootView = inflater.inflate(R.layout.fragment_mini_tracks, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new loadMiniTracks().execute("");
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        if (miniTracksAdapter != null) {
            miniTracksAdapter.notifyDataSetChanged();
        }
    }

    private class loadMiniTracks extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                miniTracksAdapter = new MiniTracksAdapter((AppCompatActivity) getActivity(), SongLoader.getMiniSongs(getActivity()), false);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(miniTracksAdapter);
            if (getActivity() != null)
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        }

        @Override
        protected void onPreExecute() {
        }
    }

}
