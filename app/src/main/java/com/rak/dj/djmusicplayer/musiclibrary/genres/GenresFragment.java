package com.rak.dj.djmusicplayer.musiclibrary.genres;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.GenreLoader;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.helpers.misc.WrappedAsyncTaskLoader;
import com.rak.dj.djmusicplayer.models.upgraded.Genre;
import com.rak.dj.djmusicplayer.musiclibrary.AbsRecyclerViewFragment;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.searchmanager.LoaderIds;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends AbsRecyclerViewFragment<GenreAdapter> implements MusicStateListener, LoaderManager.LoaderCallbacks<ArrayList<Genre>> {


    private static final int LOADER_ID = LoaderIds.GENRES_FRAGMENT;

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        ((BaseMainActivity) getActivity()).setMusicStateListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected boolean getGrid() {
        return false;
    }


    @Override
    protected GenreAdapter createAdapter() {
        ArrayList<Genre> dataSet = getAdapter() == null ? new ArrayList<Genre>() : getAdapter().getDataSet();
        return new GenreAdapter((AppCompatActivity) getActivity(), dataSet, false);
    }

    @Override
    protected String getEmptyMessage() {
        return getAppResources().getString(R.string.no_genres);
    }

    @Override
    protected boolean loadUsePalette() {
        return false;
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public Loader<ArrayList<Genre>> onCreateLoader(int id, Bundle args) {
        return new AsyncGenreLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Genre>> loader, ArrayList<Genre> data) {
        getAdapter().updateDataSet(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Genre>> loader) {
        getAdapter().updateDataSet(new ArrayList<Genre>());
    }

    private static class AsyncGenreLoader extends WrappedAsyncTaskLoader<ArrayList<Genre>> {
        public AsyncGenreLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Genre> loadInBackground() {
            return GenreLoader.getAllGenres(getContext());
        }
    }
}
