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

import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.ArtistLoader;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.helpers.SortOrder;
import com.rak.dj.djmusicplayer.models.upgraded.Artist;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;

import java.util.ArrayList;

public class ArtistFragment extends AbsRecyclerViewFragment {

    private ArtistAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;
    private PreferencesUtils mPreferences;
    private boolean isGrid;

    @Override
    public View setBaseListView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recylerview, container, false);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtils.getInstance(getActivity());
        isGrid = mPreferences.isArtistsInGrid();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        recyclerView =  rootView.findViewById(R.id.recyclerView);
        recyclerView.setEmptyView(getActivity(), rootView.findViewById(R.id.list_empty), "No media found");

        setLayoutManager();

        if (getActivity() != null)
            new loadArtists().execute("");
    }

    private void setLayoutManager() {
        if (isGrid) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setItemDecoration() {
        if (isGrid) {
            int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
            itemDecoration = new SpacesItemDecoration(spacingInPixels);
        } else {
            itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        }
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void updateLayoutManager(int column) {
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.setAdapter(new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()), loadUsePalette()));
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        setItemDecoration();
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                ArrayList<Artist> artistList = ArtistLoader.getAllArtists(getActivity());
                mAdapter.updateDataSet(artistList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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

    public boolean loadUsePalette() {
        return PreferencesUtils.getInstance(getActivity()).artistColoredFooters();
    }

    private class loadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                mAdapter = new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()), loadUsePalette());
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (mAdapter != null) {
                mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
            }
            if (getActivity() != null) {
                setItemDecoration();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.top = space;
            outRect.right = space;
            outRect.bottom = space;

        }
    }


}
