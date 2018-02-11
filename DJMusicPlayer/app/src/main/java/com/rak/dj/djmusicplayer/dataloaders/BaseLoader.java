package com.rak.dj.djmusicplayer.dataloaders;

import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * Created by sraksh on 2/11/2018.
 */

public abstract class BaseLoader {
    protected static final String BASE_AUDIO_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    protected static final String[] BASE_AUDIO_PROJECTION = new String[]{
            BaseColumns._ID,// 0
            MediaStore.Audio.AudioColumns.TITLE,// 1
            MediaStore.Audio.AudioColumns.TRACK,// 2
            MediaStore.Audio.AudioColumns.YEAR,// 3
            MediaStore.Audio.AudioColumns.DURATION,// 4
            MediaStore.Audio.AudioColumns.DATA,// 5
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 6
            MediaStore.Audio.AudioColumns.ALBUM_ID,// 7
            MediaStore.Audio.AudioColumns.ALBUM,// 8
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 9
            MediaStore.Audio.AudioColumns.ARTIST,// 10
    };

    protected static final String BASE_VIDEO_SELECTION = MediaStore.Video.Media.DATA;

    protected static final String[] BASE_VIDEO_PROJECTION = new String[]{
            BaseColumns._ID,// 0
            MediaStore.Video.Media.TITLE,// 1
            MediaStore.Video.Media.ARTIST, //2
            MediaStore.Video.Media.ALBUM,// 3
            MediaStore.Video.Media.DURATION,// 4
            MediaStore.Video.Media.DATA,// 5
            MediaStore.Video.Media.DATE_TAKEN,// 6
            MediaStore.Video.Thumbnails.DATA,// 7
            MediaStore.Video.Media.DATE_ADDED, // 8
            MediaStore.Video.Media.DATE_MODIFIED, // 9
    };

    protected static final String VIDEO_SELECTION_BY_ALBUM = MediaStore.Video.Media.ALBUM +" like ? ";

}
