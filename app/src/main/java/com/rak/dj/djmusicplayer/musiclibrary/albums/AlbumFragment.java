package com.rak.dj.djmusicplayer.musiclibrary.albums;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.AlbumLoader;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.helpers.SortOrder;
import com.rak.dj.djmusicplayer.helpers.misc.WrappedAsyncTaskLoader;
import com.rak.dj.djmusicplayer.models.upgraded.Album;
import com.rak.dj.djmusicplayer.musiclibrary.AbsRecyclerViewFragment;
import com.rak.dj.djmusicplayer.searchmanager.LoaderIds;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends AbsRecyclerViewFragment<AlbumAdapter> implements LoaderManager.LoaderCallbacks<ArrayList<Album>>{

    public static final String TAG = AlbumFragment.class.getSimpleName();

    private static final int LOADER_ID = LoaderIds.ALBUMS_FRAGMENT;

    @Override
    protected boolean getGrid() {
        return mPreferences.isAlbumsInGrid();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    protected AlbumAdapter createAdapter() {
        ArrayList<Album> dataSet = getAdapter() == null ? new ArrayList<Album>() : getAdapter().getDataSet();
        return new AlbumAdapter((AppCompatActivity) getActivity(), dataSet, loadUsePalette());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.album_sort_by, menu);
        inflater.inflate(R.menu.menu_show_as, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_year:
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_YEAR);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_artist:
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_songs:
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS);
                reloadAdapter();
                return true;
            case R.id.menu_show_as_list:
                mPreferences.setAlbumsInGrid(false);
                isGrid = false;
                updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                mPreferences.setAlbumsInGrid(true);
                isGrid = true;
                updateLayoutManager(2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadAdapter(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    @Override
    protected String getEmptyMessage() {
        return getAppResources().getString(R.string.no_albums);
    }

    @Override
    protected boolean loadUsePalette() {
        return PreferencesUtils.getInstance(getActivity()).albumColoredFooters();
    }

    @Override
    public Loader<ArrayList<Album>> onCreateLoader(int id, Bundle args) {
        return new AsyncAlbumLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Album>> loader, ArrayList<Album> data) {
        getAdapter().updateDataSet(data);
        setItemDecoration();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Album>> loader) {
        getAdapter().updateDataSet(new ArrayList<Album>());
        //setItemDecoration();
    }

    private static class AsyncAlbumLoader extends WrappedAsyncTaskLoader<ArrayList<Album>> {
        public AsyncAlbumLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Album> loadInBackground() {
            return AlbumLoader.getAllAlbums(getContext());
        }
    }
}
