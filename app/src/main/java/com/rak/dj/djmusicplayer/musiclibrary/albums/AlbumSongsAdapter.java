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

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musiclibrary.AbsRecyclerViewAdapter;
import com.rak.dj.djmusicplayer.musiclibrary.BaseViewHolder;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;

import java.util.ArrayList;

public class AlbumSongsAdapter extends AbsRecyclerViewAdapter<Song, AlbumSongsAdapter.ItemHolder> {

    private ArrayList<Song> arraylist;
    private long albumID;
    private long[] songIDs;

    public AlbumSongsAdapter(AppCompatActivity context, ArrayList<Song> arraylist, long albumID) {
        super(context, arraylist);
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
        this.albumID = albumID;
    }

    @Override
    public String getAteKey() {
        return ateKey = Helpers.getATEKey(mContext);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void genericBindViewHolder(ItemHolder itemHolder, int position) {
        Song localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.duration.setText(JazzUtil.makeShortTimeString(mContext, (localItem.duration) / 1000));
        int tracknumber = localItem.trackNumber;
        if (tracknumber == 0) {
            itemHolder.trackNumber.setText("-");
        } else itemHolder.trackNumber.setText(String.valueOf(tracknumber));

        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(Config.accentColor(mContext, ateKey));
        }else{
            itemHolder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));
        }

        setOnPopupMenuListener(itemHolder, position);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.menu.setOnClickListener(v -> {

            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.setOnMenuItemClickListener(menuItem ->  {

                menuFunctionalityForSong(mContext, menuItem, position, -1, songIDs);

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
    public void updateDataSet(ArrayList<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
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
                    JazzUtil.IdType.Album, false,
                    arraylist.get(getAdapterPosition()), true), 100);
        }

    }

}



