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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.ArtistLoader;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.playlistmanager.AddPlaylistDialog;

import java.util.ArrayList;
import java.util.List;

public class ArtistSongAdapter extends AbsSongsAdapter<Song, ArtistSongAdapter.ItemHolder> {

    private ArrayList<Song> arraylist;
    private AppCompatActivity mContext;
    private int artistID;
    private long[] songIDs;
    private String ateKey;
    public ArtistSongAdapter(AppCompatActivity context, ArrayList<Song> arraylist, int artistID) {
        super(context, arraylist);
        this.arraylist = arraylist;
        this.mContext = context;
        this.artistID = artistID;
        this.songIDs = getSongIds();
        this.ateKey = Helpers.getATEKey(context);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_detail_albums_header, null);
            ItemHolder ml = new ItemHolder(v0);
            return ml;
        } else {
            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_song, null);
            ItemHolder ml = new ItemHolder(v2);
            return ml;
        }
    }

    @Override
    public void genericBindViewHolder(ItemHolder itemHolder, int i) {

        if (getItemViewType(i) == 0) {
            //nothing
            setUpAlbums(itemHolder.albumsRecyclerView);
        } else {
            Song localItem = arraylist.get(i);
            itemHolder.title.setText(localItem.title);
            itemHolder.album.setText(localItem.albumName);

            ImageLoader.getInstance().displayImage(JazzUtil.getAlbumArtUri(localItem.albumId).toString(),
                    itemHolder.albumArt, new DisplayImageOptions.Builder()
                            .cacheInMemory(true).showImageOnLoading(R.drawable.ic_empty_music2).resetViewBeforeLoading(true).build());

            if (MusicPlayer.getCurrentAudioId() == localItem.id) {
                itemHolder.title.setTextColor(Config.accentColor(mContext, ateKey));
            }else{
                itemHolder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));
            }
            setOnPopupMenuListener(itemHolder, i - 1);
        }
    }

    @Override
    public void onViewRecycled(ItemHolder itemHolder) {

        if (itemHolder.getItemViewType() == 0)
            clearExtraSpacingBetweenCards(itemHolder.albumsRecyclerView);

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    protected void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.menu.setOnClickListener(v -> {

            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.setOnMenuItemClickListener(item -> {

                //Commented because of the song position is changed, so need to rewrite this logic for common menu options
                //menuFunctionalityForSong(mContext, item, arraylist.get(position), -1, songIDs);

                switch (item.getItemId()) {
                    case R.id.popup_song_play:
                        MusicPlayer.playAll(mContext, songIDs, position, -1, JazzUtil.IdType.NA, false);
                        break;
                    case R.id.popup_song_play_next:
                        long[] ids = new long[1];
                        ids[0] = arraylist.get(position + 1).id;
                        MusicPlayer.playNext(mContext, ids, -1, JazzUtil.IdType.NA);
                        break;
                    case R.id.popup_song_goto_album:
                        NavigationUtil.goToAlbum(mContext, arraylist.get(position + 1).albumId);
                        break;
                    case R.id.popup_song_goto_artist:
                        NavigationUtil.goToArtist(mContext, arraylist.get(position + 1).artistId);
                        break;
                    case R.id.popup_song_addto_queue:
                        long[] id = new long[1];
                        id[0] = arraylist.get(position + 1).id;
                        MusicPlayer.addToQueue(mContext, id, -1, JazzUtil.IdType.NA);
                        break;
                    case R.id.popup_song_addto_playlist:
                        AddPlaylistDialog.newInstance(arraylist.get(position + 1)).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ADD_PLAYLIST");
                        break;
                    case R.id.popup_song_share:
                        JazzUtil.shareTrack(mContext, arraylist.get(position).id);
                        break;
                    case R.id.popup_tag_editor:
                        NavigationUtil.navigateToSongTagEditor(mContext, arraylist.get(position).id);
                        break;
                    case R.id.popup_song_delete:
                        long[] deleteIds = {arraylist.get(position + 1).id};
                        JazzUtil.showDeleteDialog(mContext,arraylist.get(position + 1).title, deleteIds, ArtistSongAdapter.this, position + 1);
                        break;
                }
                return false;
            });
            menu.inflate(R.menu.popup_song);
            menu.show();
        });
    }

    private void setUpAlbums(RecyclerView albumsRecyclerview) {

        albumsRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        albumsRecyclerview.setHasFixedSize(true);

        //to add spacing between cards
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_card);
        albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        albumsRecyclerview.setNestedScrollingEnabled(false);


        ArtistAlbumAdapter mAlbumAdapter = new ArtistAlbumAdapter(mContext, ArtistLoader.getArtist(mContext, artistID).albums);
        albumsRecyclerview.setAdapter(mAlbumAdapter);
    }

    private void clearExtraSpacingBetweenCards(RecyclerView albumsRecyclerview) {
        //to clear any extra spacing between cards
        int spacingInPixelstoClear = -(mContext.getResources().getDimensionPixelSize(R.dimen.spacing_card));
        albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixelstoClear));

    }

    public long[] getSongIds() {
        List<Song> actualArraylist = new ArrayList<Song>(arraylist);
        //actualArraylist.remove(0);
        long[] ret = new long[actualArraylist.size()];
        for (int i = 0; i < actualArraylist.size(); i++) {
            ret[i] = actualArraylist.get(i).id;
        }
        return ret;
    }

    @Override
    public void removeSongAt(int i){
        arraylist.remove(i);
        updateDataSet(arraylist);
    }

    @Override
    public void updateDataSet(ArrayList<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (position == 0) {
            viewType = 0;
        } else viewType = 1;
        return viewType;
    }

    public class ItemHolder extends BaseViewHolder implements View.OnClickListener {
        protected TextView title, album;
        protected ImageView albumArt, menu;
        protected RecyclerView albumsRecyclerView;

        public ItemHolder(View view) {
            super(view);

            this.albumsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_album);

            this.title = (TextView) view.findViewById(R.id.song_title);
            this.album = (TextView) view.findViewById(R.id.song_album);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            this.menu = (ImageView) view.findViewById(R.id.popup_menu);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAll(mContext, songIDs, getAdapterPosition() - 1, artistID,
                            JazzUtil.IdType.Artist, false,
                            arraylist.get(getAdapterPosition()), true);
                }
            }, 100);

        }

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            //the padding from left
            outRect.left = space;


        }
    }
}




