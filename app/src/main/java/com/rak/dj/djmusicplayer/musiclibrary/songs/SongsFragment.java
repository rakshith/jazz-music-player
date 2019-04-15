package com.rak.dj.djmusicplayer.musiclibrary.songs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.SongLoader;
import com.rak.dj.djmusicplayer.helpers.SortOrder;
import com.rak.dj.djmusicplayer.helpers.misc.WrappedAsyncTaskLoader;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musiclibrary.AbsRecyclerViewFragment;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.searchmanager.LoaderIds;

import java.util.ArrayList;


public class SongsFragment extends AbsRecyclerViewFragment<SongsAdapter> implements MusicStateListener, LoaderManager.LoaderCallbacks<ArrayList<Song>> {

    public static final String TAG = SongsFragment.class.getSimpleName();

    private static final int LOADER_ID = LoaderIds.SONGS_FRAGMENT;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseMainActivity) getActivity()).setMusicStateListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected boolean getGrid() {
        return false;
    }

    @NonNull
    @Override
    protected SongsAdapter createAdapter() {
        ArrayList<Song> dataSet = getAdapter() == null ? new ArrayList<Song>() : (ArrayList<Song>) getAdapter().getDataSet();
        return new SongsAdapter((AppCompatActivity) getActivity(), dataSet, false, false);
    }

    @Override
    protected String getEmptyMessage() {
        return getAppResources().getString(R.string.no_songs);
    }

    @Override
    protected boolean loadUsePalette() {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.song_sort_by, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_artist:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_album:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_year:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_duration:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION);
                reloadAdapter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        getAdapter().notifyDataSetChanged();
        //reloadAdapter();
    }

    private void reloadAdapter() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<Song>> onCreateLoader(int id, Bundle args) {
        return new AsyncSongsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Song>> loader, ArrayList<Song> data) {
        getAdapter().updateDataSet(data);
        setItemDecoration();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Song>> loader) {
        getAdapter().updateDataSet(new ArrayList<>());
        //setItemDecoration();
    }


    private static class AsyncSongsLoader extends WrappedAsyncTaskLoader<ArrayList<Song>> {
        public AsyncSongsLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Song> loadInBackground() {
            return SongLoader.getAllSongs(getContext());
        }
    }

}
