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

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.models.Song;

import java.util.List;

public class AlbumSongsAdapter extends AbsSongsAdapter<Song, AlbumSongsAdapter.ItemHolder> {

    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long albumID;
    private long[] songIDs;
    private int itemPosition;

    public AlbumSongsAdapter(AppCompatActivity context, List<Song> arraylist, long albumID) {
        super(context, arraylist);
        this.arraylist = arraylist;
        this.mContext = context;
        this.songIDs = getSongIds();
        this.albumID = albumID;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void genericBindViewHolder(ItemHolder itemHolder, int position) {
        this.itemPosition = position;
        Song localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.duration.setText(JazzUtils.makeShortTimeString(mContext, (localItem.duration) / 1000));
        int tracknumber = localItem.trackNumber;
        if (tracknumber == 0) {
            itemHolder.trackNumber.setText("-");
        } else itemHolder.trackNumber.setText(String.valueOf(tracknumber));

        setOnPopupMenuListener(itemHolder, position);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.menu.setOnClickListener(v -> {

            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.setOnMenuItemClickListener(menuItem ->  {

                menuFunctionalityForSong(mContext, menuItem, arraylist.get(position), -1, songIDs);

                /*switch (menuItem.getItemId()) {
                    case R.id.popup_song_play:
                        MusicPlayer.playAll(mContext, songIDs, position, -1, JazzUtils.IdType.NA, false);
                        break;
                    case R.id.popup_song_play_next:
                        long[] ids = new long[1];
                        ids[0] = arraylist.get(position).id;
                        MusicPlayer.playNext(mContext, ids, -1, JazzUtils.IdType.NA);
                        break;
                    case R.id.popup_song_goto_album:
                        NavigationUtils.goToAlbum(mContext, arraylist.get(position).albumId);
                        break;
                    case R.id.popup_song_goto_artist:
                        NavigationUtils.goToArtist(mContext, arraylist.get(position).artistId);
                        break;
                    case R.id.popup_song_addto_queue:
                        long[] id = new long[1];
                        id[0] = arraylist.get(position).id;
                        MusicPlayer.addToQueue(mContext, id, -1, JazzUtils.IdType.NA);
                        break;
                    case R.id.popup_song_addto_playlist:
                        AddPlaylistDialog.newInstance(arraylist.get(position)).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ADD_PLAYLIST");
                        break;
                    case R.id.popup_song_share:
                        JazzUtils.shareTrack(mContext, arraylist.get(position).id);
                        break;
                    case R.id.popup_tag_editor:
                        NavigationUtils.navigateToSongTagEditor(mContext, arraylist.get(position).id);
                        break;
                    case R.id.popup_song_delete:
                        long[] deleteIds = {arraylist.get(position).id};
                        JazzUtils.showDeleteDialog(mContext,arraylist.get(position).title, deleteIds, AlbumSongsAdapter.this, position);
                        break;
                }*/
                return false;
            });
            menu.inflate(R.menu.popup_song);
            menu.show();
        });
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    @Override
    public void updateDataSet(List<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
    }

    @Override
    public int getItemPosition() {
        return itemPosition;
    }

    @Override
    public void removeSongAt(int i){
        arraylist.remove(i);
    }

    public class ItemHolder extends BaseViewHolder implements View.OnClickListener {
        protected TextView title, duration, trackNumber;
        protected ImageView menu;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.duration = (TextView) view.findViewById(R.id.song_duration);
            this.trackNumber = (TextView) view.findViewById(R.id.trackNumber);
            this.menu = (ImageView) view.findViewById(R.id.popup_menu);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(() -> playAll(mContext, songIDs, getAdapterPosition(), albumID,
                    JazzUtils.IdType.Album, false,
                    arraylist.get(getAdapterPosition()), true), 100);
        }

    }

}



