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

package com.rak.dj.djmusicplayer.queuemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.QueueLoader;
import com.rak.dj.djmusicplayer.helpers.ThemeStore;
import com.rak.dj.djmusicplayer.helpers.ViewUtil;
import com.rak.dj.djmusicplayer.helpers.misc.WrappedAsyncTaskLoader;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.searchmanager.LoaderIds;
import com.rak.dj.djmusicplayer.widgets.BaseRecyclerView;
import com.rak.dj.djmusicplayer.widgets.DragSortRecycler;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class QueueFragment extends Fragment implements MusicStateListener, LoaderManager.LoaderCallbacks<List<Song>> {

    private static final int LOADER_ID = LoaderIds.QUEUE_FRAGMENT;

    private PlayingQueueAdapter mAdapter;

    private Unbinder unbinder;

    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;

    @BindView(R.id.list_empty)
    TextView empty;

    DragSortRecycler dragSortRecycler;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.playing_queue);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);

        if (recyclerView instanceof FastScrollRecyclerView) {
            ViewUtil.setUpFastScrollRecyclerViewColor(getActivity(), ((FastScrollRecyclerView) recyclerView), ThemeStore.accentColor(getActivity()));
        }

        List<Song> dataSet = mAdapter == null ? new ArrayList<Song>() :mAdapter.getDataSet();
        mAdapter = new PlayingQueueAdapter((AppCompatActivity) getActivity(), dataSet);
        recyclerView.setAdapter(mAdapter);
        dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.reorder);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIsEmpty();
            }
        });

        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                Log.d("queue", "onItemMoved " + from + " to " + to);
                Song song = mAdapter.getSongAt(from);
                mAdapter.removeSongAt(from);
                mAdapter.addSongTo(to, song);
                mAdapter.notifyDataSetChanged();
                MusicPlayer.moveQueueItem(from, to);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
        ((BaseMainActivity) getActivity()).setMusicStateListener(this);

        return rootView;
    }

    private void checkIsEmpty() {
        if (empty != null) {
            empty.setText(getEmptyMessage());
            empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private String getEmptyMessage() {
        return getResources().getString(R.string.no_songs_queue);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(getActivity(), "dark_theme");
        } else {
            ATE.apply(getActivity(), "light_theme");
        }
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<List<Song>> onCreateLoader(int id, Bundle args) {
        return new AsyncSongLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Song>> loader, List<Song> data) {
        mAdapter.updateDataSet(data);
        recyclerView.addItemDecoration(dragSortRecycler);
        recyclerView.addOnItemTouchListener(dragSortRecycler);
        recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());
        recyclerView.getLayoutManager().scrollToPosition(mAdapter.currentlyPlayingPosition);
    }

    @Override
    public void onLoaderReset(Loader<List<Song>> loader) {
        mAdapter.updateDataSet(new ArrayList<>());
    }

    private static class AsyncSongLoader extends WrappedAsyncTaskLoader<List<Song>> {
        public AsyncSongLoader(Context context) {
            super(context);
        }

        @Override
        public List<Song> loadInBackground() {
            return QueueLoader.getQueueSongs(getContext());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

