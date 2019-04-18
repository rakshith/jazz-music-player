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
package com.rak.dj.djmusicplayer.musiclibrary.albums;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kabouzeid.appthemehelper.ATH;
import com.rak.dj.djmusicplayer.BaseMainActivity;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.AlbumLoader;
import com.rak.dj.djmusicplayer.glide.JazzColoredTarget;
import com.rak.dj.djmusicplayer.glide.SongGlideRequest;
import com.rak.dj.djmusicplayer.glide.palette.BitmapPaletteWrapper;
import com.rak.dj.djmusicplayer.helpers.ColorUtil;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.MaterialValueHelper;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.helpers.SortOrder;
import com.rak.dj.djmusicplayer.models.upgraded.Album;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musiclibrary.AbsThemedMusicLibraryFragment;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.playlistmanager.AddPlaylistDialog;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;

import java.util.ArrayList;

import static com.rak.dj.djmusicplayer.helpers.ATEUtils.setStatusBarColor;

public class AlbumDetailFragment extends AbsThemedMusicLibraryFragment implements MusicStateListener {

    private int albumID = -1;

    private ImageView albumArt, artistArt;
    private TextView albumTitle, albumBy, albumLength, albumReleased;
    private AppCompatActivity mContext;

    private RecyclerView recyclerView;
    private AlbumSongsAdapter mAdapter;

    private Album album;

    // private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    private boolean loadFailed = false;

    private PreferencesUtils mPreferences;
    private Context context;
    private int toolBarColor = -1;

    public static AlbumDetailFragment newInstance(int id, boolean useTransition, String transitionName) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ALBUM_ID, id);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getInt(Constants.ALBUM_ID);
        }
        context = getActivity();
        mContext = (AppCompatActivity) context;
        mPreferences = PreferencesUtils.getInstance(context);
    }


    @TargetApi(21)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_album_detail_v2, container, false);
        ((BaseMainActivity) getActivity()).setMusicStateListener(this);
        albumArt = rootView.findViewById(R.id.album_art);
        artistArt = rootView.findViewById(R.id.artist_art);
        albumTitle = rootView.findViewById(R.id.album_title);
        albumBy = rootView.findViewById(R.id.albumBy);
        albumLength = rootView.findViewById(R.id.albumLength);
        albumReleased = rootView.findViewById(R.id.albumReleased);

        toolbar = rootView.findViewById(R.id.toolbar);
        appBarLayout = rootView.findViewById(R.id.app_bar);
        fab = rootView.findViewById(R.id.fab);

        if (getArguments().getBoolean("transition")) {
            albumArt.setTransitionName(getArguments().getString("transition_name"));
        }
        recyclerView = rootView.findViewById(R.id.recyclerview);
        // collapsingToolbarLayout = rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout = rootView.findViewById(R.id.app_bar);
        recyclerView.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        album = AlbumLoader.getAlbum(getActivity(), albumID);

        setUpEverything();

        fab.setOnClickListener( v ->{
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                AlbumSongsAdapter adapter = (AlbumSongsAdapter) recyclerView.getAdapter();
                MusicPlayer.playAll(getActivity(), adapter.getSongIds(), 0, albumID, JazzUtil.IdType.Album, true);
                NavigationUtil.navigateToNowplaying(getActivity(), false);
            }, 150);
        });

        return rootView;
    }

    private Album getAlbum() {
        if (album == null) album = new Album();
        return album;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Album");
        //collapsingToolbarLayout.setTitle(album.getTitle());
    }

    private void loadAlbumCover() {
        SongGlideRequest.Builder.from(Glide.with(this), getAlbum().safeGetFirstSong())
                .checkIgnoreMediaStore(getActivity())
                .generatePalette(getActivity()).build()
                .dontAnimate()
                .listener(new RequestListener<Object, BitmapPaletteWrapper>() {
                    @Override
                    public boolean onException(Exception e, Object model, Target<BitmapPaletteWrapper> target, boolean isFirstResource) {
                        getActivity().supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(BitmapPaletteWrapper resource, Object model, Target<BitmapPaletteWrapper> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getActivity().supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(new JazzColoredTarget(albumArt) {
                    @Override
                    public void onColorReady(int color) {
                        setColors(color);
                    }
                });
    }

    private void setColors(int color) {
        toolBarColor = color;
        toolbar.setBackgroundColor(color);
        int secondaryTextColor = MaterialValueHelper.getSecondaryTextColor(getActivity(), ColorUtil.isColorLight(color));
        int primaryTextColor = MaterialValueHelper.getPrimaryTextColor(getActivity(), ColorUtil.isColorLight(color));

        String ateKey = Helpers.getATEKey(getActivity());
        setStatusBarColor(getActivity(), ateKey , color);

        // ATH.setNavigationbarColor(getActivity(), color);

        ATH.setTaskDescriptionColor(getActivity(), primaryTextColor);

        appBarLayout.setBackgroundColor(color);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar); // needed to auto readjust the toolbar content color


        albumTitle.setTextColor(primaryTextColor);
        albumBy.setTextColor(secondaryTextColor);
        albumLength.setTextColor(secondaryTextColor);
        albumReleased.setTextColor(secondaryTextColor);
    }

    @Override
    public void onResume() {
        super.onResume();
        setColors(toolBarColor);
    }

    private void setAlbumDetails() {

        String songCount = JazzUtil.makeLabel(getActivity(), R.plurals.Nsongs, album.getSongCount());

        String year = (album.getYear() != 0) ? (" - " + String.valueOf(album.getYear())) : "";

        albumTitle.setText(album.getTitle());
        albumBy.setText("Album by: "+album.getArtistName());
        albumLength.setText("Length: "+songCount);

        if ((year != null)) {
            albumReleased.setText("Released: " + year);
        } else {
            albumReleased.setText("Released: " + "-");
        }


    }

    private void setUpAlbumSongs() {
        Album album = AlbumLoader.getAlbum(getActivity(), albumID);
        mAdapter = new AlbumSongsAdapter((AppCompatActivity) getActivity(), album.songs, albumID);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
    }

    private void setUpEverything() {
        setupToolbar();
        setAlbumDetails();
        setUpAlbumSongs();
        loadAlbumCover();
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                ArrayList<Song> songList = AlbumLoader.getAlbum(getActivity(), albumID).songs;
                mAdapter.updateDataSet(songList);
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
        inflater.inflate(R.menu.album_detail, menu);
        if (getActivity() != null)
            ATE.applyMenu(getActivity(), Helpers.getATEKey(getActivity()), menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_go_to_artist:
                NavigationUtil.goToArtist(getContext(), album.getId());
                break;
            case R.id.popup_song_addto_queue:
                MusicPlayer.addToQueue(context, mAdapter.getSongIds(), -1, JazzUtil.IdType.NA);
                break;
            case R.id.popup_song_addto_playlist:
                AddPlaylistDialog.newInstance(mAdapter.getSongIds()).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                break;
            case R.id.menu_sort_by_az:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_year:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_YEAR);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_duration:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_DURATION);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_track_number:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
                reloadAdapter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }


    @Override
    public void onMetaChanged() {
        AlbumSongsAdapter adapter = (AlbumSongsAdapter) recyclerView.getAdapter();
        adapter.notifyDataSetChanged();
    }



    /*private class EnterTransitionListener extends SimplelTransitionListener {

        @TargetApi(21)
        public void onTransitionEnd(Transition paramTransition) {
            FabAnimationUtils.scaleIn(fab);
        }

        public void onTransitionStart(Transition paramTransition) {
            FabAnimationUtils.scaleOut(fab, 0, null);
        }
    }*/
}
