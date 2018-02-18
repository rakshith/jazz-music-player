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

package com.rak.dj.djmusicplayer.searchmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.BaseThemedActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.AlbumLoader;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.ArtistLoader;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.SongLoader;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.ThemeStore;
import com.rak.dj.djmusicplayer.helpers.misc.WrappedAsyncTaskLoader;
import com.rak.dj.djmusicplayer.providers.SearchHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseThemedActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<Object>> {
    public static final String TAG = SearchActivity.class.getSimpleName();
    public static final String QUERY = "query";
    private static final int LOADER_ID = LoaderIds.SEARCH_ACTIVITY;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.empty)
    TextView empty;

    SearchView searchView;

    private SearchAdapter adapter;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //setDrawUnderStatusbar(true);
        ButterKnife.bind(this);

        //setStatusbarColorAuto();
        //setNavigationbarColorAuto();
        //setTaskDescriptionColorAuto();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this, Collections.emptyList());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                empty.setVisibility(adapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });

        setUpToolBar();

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY);
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY, query);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setUpToolBar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_library));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        MenuItemCompat.expandActionView(searchItem);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return false;
            }
        });

        searchView.setQuery(query, false);
        searchView.post(() -> searchView.setOnQueryTextListener(SearchActivity.this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(@NonNull String query) {
        this.query = query;
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        hideSoftKeyboard();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return false;
    }

    private void hideSoftKeyboard() {
        JazzUtil.hideSoftKeyboard(SearchActivity.this);
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        return new AsyncSearchResultLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        adapter.swapDataSet(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        adapter.swapDataSet(Collections.emptyList());
    }

    private static class AsyncSearchResultLoader extends WrappedAsyncTaskLoader<List<Object>> {
        private final String query;

        public AsyncSearchResultLoader(Context context, String query) {
            super(context);
            this.query = query;
        }

        @Override
        public List<Object> loadInBackground() {
            List<Object> results = new ArrayList<>();
            if (!TextUtils.isEmpty(query)) {
                List songs = SongLoader.getSongs(getContext(), query);
                if (!songs.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.songs));
                    results.addAll(songs);
                }

                List artists = ArtistLoader.getArtists(getContext(), query);
                if (!artists.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.artists));
                    results.addAll(artists);
                }

                List albums = AlbumLoader.getAlbums(getContext(), query);
                if (!albums.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.albums));
                    results.addAll(albums);
                }
            }
            return results;
        }
    }
}
