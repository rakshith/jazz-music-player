/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2015 Naman Dwivedi
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.rak.dj.djmusicplayer.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;


import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.models.upgraded.Song;

import java.util.ArrayList;
import java.util.List;

public class LastAddedLoader {

    private static Cursor mCursor;

    public static List<Song> getLastAddedSongs(Context context) {

        ArrayList<Song> mSongList = new ArrayList<>();
        mCursor = makeLastAddedCursor(context);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                int id = mCursor.getInt(0);
                String title = mCursor.getString(1);
                String artist = mCursor.getString(2);
                String album = mCursor.getString(3);
                int duration = mCursor.getInt(4);
                int trackNumber = mCursor.getInt(5);
                int artistId = mCursor.getInt(6);
                int albumId = mCursor.getInt(7);
                String data = mCursor.getString(8);
                final Song song = new Song(id, title, trackNumber, -1, duration, data, -1, albumId, album, artistId, artist);

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    public static final Cursor makeLastAddedCursor(final Context context) {
        //four weeks ago
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);
        long cutoff = PreferencesUtils.getInstance(context).getLastAddedCutoff();
        // use the most recent of the two timestamps
        if (cutoff < fourWeeksAgo) {
            cutoff = fourWeeksAgo;
        }

        final StringBuilder selection = new StringBuilder();
        selection.append(AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + AudioColumns.TITLE + " != ''");
        selection.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + ">");
        selection.append(cutoff);

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.AudioColumns.TRACK,
                        MediaStore.Audio.AudioColumns.ARTIST_ID,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA}, selection.toString(), null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }
}
