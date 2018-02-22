package com.rak.dj.djmusicplayer.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.models.AlbumVideo;
import com.rak.dj.djmusicplayer.models.Video;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.rak.dj.djmusicplayer.dataloaders.BaseLoader.BASE_VIDEO_PROJECTION;
import static com.rak.dj.djmusicplayer.dataloaders.BaseLoader.VIDEO_SELECTION_BY_ALBUM;

/**
 * Created by sraksh on 2/11/2018.
 */

public class VideoLoader {

    public static ArrayList<Video> getAllVideo(Context context) {
        return getVideosForCursor(makeVideoCursor(context, null, null));
    }

    public static ArrayList<Video> getVideosForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                String data = cursor.getString(5);
                String dateTaken = cursor.getString(6);
                String minThumbMagic = cursor.getString(7);
                String dateAdded = cursor.getString(8);
                String dateModified = cursor.getString(8);
                arrayList.add(new Video(id, title, duration, album, artist, data, dateTaken, minThumbMagic, dateAdded, dateModified));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }


    @Nullable
    private static Cursor makeVideoAlbumCursor(@NonNull final Context context, String albumName) {
        try {
            return context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    BASE_VIDEO_PROJECTION, VIDEO_SELECTION_BY_ALBUM, new String[]{"%"+ albumName +"%"}, PreferencesUtils.getInstance(context).getVideoSortOrder());
        } catch (SecurityException e) {
            return null;
        }
    }

    @NonNull
    public static ArrayList<Video> getVideos(@NonNull final Context context, final String albumName) {
        return getVideosForCursor(makeVideoAlbumCursor(context, albumName));
    }


    public static Cursor makeVideoCursor(Context context, String selection, String[] paramArrayOfString) {
        final String videoSortOrder = PreferencesUtils.getInstance(context).getVideoSortOrder();
        return makeVideoCursor(context, selection, paramArrayOfString, videoSortOrder);
    }

    private static Cursor makeVideoCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        return context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, BASE_VIDEO_PROJECTION, null, paramArrayOfString, sortOrder);
    }

    public static ArrayList<AlbumVideo> getAlbumVideos(Context context){
        ArrayList<Video> videos = getAllVideo(context);

        HashSet<Video> uniqueSet = new HashSet<>(videos);

        ArrayList<Video> uniqueList = new ArrayList<>(uniqueSet);
        ArrayList<AlbumVideo> albumVideos = new ArrayList<>();

        // This works only in Android 7.0 above and 6.0 gives runtime exception
        uniqueList.forEach(video -> {
            int videoCount = getVideos(context, video.albumName).size();
            albumVideos.add(new AlbumVideo(video.id, video.albumName, videoCount));
        });

        return albumVideos;

    }

}
