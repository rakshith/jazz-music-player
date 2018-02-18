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

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rak.dj.djmusicplayer.glide.ArtistGlideRequest;
import com.rak.dj.djmusicplayer.glide.JazzColoredTarget;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.lastfmapi.upgradedapi.rest.LastFMRestClient;
import com.rak.dj.djmusicplayer.models.upgraded.Artist;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.playlistmanager.AddPlaylistDialog;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.ArtistLoader;
import com.rak.dj.djmusicplayer.helpers.Constants;

import java.util.List;

public class ArtistDetailFragment extends AbsThemedMusicLibraryFragment {

    private int artistID = -1;
    private ImageView artistArt;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private boolean largeImageLoaded = false;
    private int primaryColor = -1;
    private ArtistSongAdapter mAdapter;
    private Artist artist;
    private boolean forceDownload;
    private LastFMRestClient lastFMRestClient;
    public static ArtistDetailFragment newInstance(int id, boolean useTransition, String transitionName) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARTIST_ID, id);
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
            artistID = getArguments().getInt(Constants.ARTIST_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_artist_detail, container, false);

        artistArt = (ImageView) rootView.findViewById(R.id.artist_art);

        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);

        if (getArguments().getBoolean("transition")) {
            artistArt.setTransitionName(getArguments().getString("transition_name"));
        }

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        setupToolbar();

        setUpArtistDetails();

        getChildFragmentManager().beginTransaction().replace(R.id.container, ArtistMusicFragment.newInstance(artistID)).commit();


        return rootView;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setColors(int color) {
        //toolbarColor = color;
        //artistName.setBackgroundColor(color);
       // artistName.setTextColor(MaterialValueHelper.getPrimaryTextColor(this, ColorUtil.isColorLight(color)));
        //setNavigationbarColor(color);
        //setTaskDescriptionColor(color);
    }


    private Artist getArtist() {
        if (artist == null) artist = new Artist();
        return artist;
    }

    private void loadArtistImage() {
        ArtistGlideRequest.Builder.from(Glide.with(this), artist)
                .forceDownload(forceDownload)
                .generatePalette(getActivity()).build()
                .dontAnimate()
                .into(new JazzColoredTarget(artistArt) {
                    @Override
                    public void onColorReady(int color) {
                        setColors(color);
                    }
                });
        forceDownload = false;
    }

    private void setUpArtistDetails() {
        artist = ArtistLoader.getArtist(getActivity(), artistID);
        List<Song> songList = artist.getSongs();
        mAdapter = new ArtistSongAdapter((AppCompatActivity) getActivity(), songList, artistID);

        collapsingToolbarLayout.setTitle(artist.getName());

        loadArtistImage();

        /*if (Util.isAllowedToDownloadMetadata(this)) {
            loadBiography();
        }*/

        //artistName.setText(artist.getName());
        //songAdapter.swapDataSet(artist.getSongs());
        //albumAdapter.swapDataSet(artist.albums);

       /* LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(artist.name), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(final LastfmArtist artist) {
                if (artist != null) {

                    ImageLoader.getInstance().displayImage(artist.mArtwork.get(4).mUrl, artistArt,
                            new DisplayImageOptions.Builder().cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .showImageOnFail(R.drawable.ic_empty_music2)
                                    .build(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    largeImageLoaded = true;
                                    try {
                                        new Palette.Builder(loadedImage).generate(palette -> {
                                            Palette.Swatch swatch = palette.getVibrantSwatch();
                                            if (swatch != null) {
                                                primaryColor = swatch.getRgb();
                                                collapsingToolbarLayout.setContentScrimColor(primaryColor);
                                                if (getActivity() != null)
                                                    ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
                                            } else {
                                                Palette.Swatch swatchMuted = palette.getMutedSwatch();
                                                if (swatchMuted != null) {
                                                    primaryColor = swatchMuted.getRgb();
                                                    collapsingToolbarLayout.setContentScrimColor(primaryColor);
                                                    if (getActivity() != null)
                                                        ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
                                                }
                                            }

                                        });
                                    } catch (Exception ignored) {

                                    }
                                }
                            });
                    Handler handler = new Handler();
                    handler.postDelayed(() -> setBlurredPlaceholder(artist), 100);

                }
            }

            @Override
            public void artistInfoFailed() {

            }
        });*/

    }

    /*private void setBlurredPlaceholder(LastfmArtist artist) {
        ImageLoader.getInstance().loadImage(artist.mArtwork.get(1).mUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (getActivity() != null && !largeImageLoaded)
                    new setBlurredAlbumArt().execute(loadedImage);

            }
        });
    }*/

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artist_detail, menu);
        //if (getActivity() != null)
            //ATE.applyMenu(getActivity(), "dark_theme", menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popup_song_addto_queue:
                MusicPlayer.addToQueue(getContext(), mAdapter.getSongIds(), -1, JazzUtil.IdType.NA);
                break;
            case R.id.popup_song_addto_playlist:
                AddPlaylistDialog.newInstance(mAdapter.getSongIds()).show(getActivity().getSupportFragmentManager(), "ADD_PLAYLIST");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /*private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtil.createBlurredImageFromBitmap(loadedImage[0], getActivity(), 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null && !largeImageLoaded) {
                artistArt.setImageDrawable(result);
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();

    }


}