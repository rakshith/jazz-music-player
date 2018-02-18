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

package com.rak.dj.djmusicplayer.dataloaders;

import android.content.Context;


import com.rak.dj.djmusicplayer.models.upgraded.Song;

import java.util.ArrayList;
import java.util.List;


public class QueueLoader {


    private static NowPlayingCursor mCursor;

    public static List<Song> getQueueSongs(Context context) {

        final ArrayList<Song> mSongList = new ArrayList<>();
        mCursor = new NowPlayingCursor(context);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final int id = mCursor.getInt(0);

                final String title = mCursor.getString(1);

                final String artistName = mCursor.getString(2);

                final int albumId = mCursor.getInt(3);

                final String albumName = mCursor.getString(4);

                final long duration = mCursor.getLong(5);

                final int artistId = mCursor.getInt(7);

                final int trackNumber = mCursor.getInt(6);

                final String data = mCursor.getString(7);
                final Song song = new Song(id, title, trackNumber, -1, duration, data, -1, albumId, albumName, artistId, artistName);

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }



}
