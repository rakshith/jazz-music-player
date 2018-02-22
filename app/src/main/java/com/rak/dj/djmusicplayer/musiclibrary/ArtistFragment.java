/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.rak.dj.djmusicplayer.musiclibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.ArtistLoader;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.helpers.SortOrder;
import com.rak.dj.djmusicplayer.helpers.misc.WrappedAsyncTaskLoader;
import com.rak.dj.djmusicplayer.models.upgraded.Artist;
import com.rak.dj.djmusicplayer.searchmanager.LoaderIds;

import java.util.ArrayList;

public class ArtistFragment extends AbsRecyclerViewFragment<ArtistAdapter> implements LoaderManager.LoaderCallbacks<ArrayList<Artist>>{

    public static final String TAG = ArtistFragment.class.getSimpleName();

    private static final int LOADER_ID = LoaderIds.ARTISTS_FRAGMENT;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected boolean getGrid() {
        return mPreferences.isArtistsInGrid();
    }

    @Override
    protected ArtistAdapter createAdapter() {
        ArrayList<Artist> dataSet = getAdapter() == null ? new ArrayList<Artist>() : getAdapter().getDataSet();
        return new ArtistAdapter(getActivity(), dataSet, loadUsePalette());
    }

    @Override
    protected String getEmptyMessage() {
        return getAppResources().getString(R.string.no_artists);
    }

    @Override
    protected boolean loadUsePalette() {
        return PreferencesUtils.getInstance(getActivity()).artistColoredFooters();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artist_sort_by, menu);
        inflater.inflate(R.menu.menu_show_as, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_songs:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_albums:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
                reloadAdapter();
                return true;
            case R.id.menu_show_as_list:
                mPreferences.setArtistsInGrid(false);
                isGrid = false;
                updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                mPreferences.setArtistsInGrid(true);
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
    public Loader<ArrayList<Artist>> onCreateLoader(int id, Bundle args) {
        return new AsyncArtistLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Artist>> loader, ArrayList<Artist> data) {
        getAdapter().updateDataSet(data);
        setItemDecoration();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Artist>> loader) {
        getAdapter().updateDataSet(new ArrayList<Artist>());
        //setItemDecoration();
    }


    private static class AsyncArtistLoader extends WrappedAsyncTaskLoader<ArrayList<Artist>> {
        public AsyncArtistLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Artist> loadInBackground() {
            return ArtistLoader.getAllArtists(getContext());
        }
    }
}
