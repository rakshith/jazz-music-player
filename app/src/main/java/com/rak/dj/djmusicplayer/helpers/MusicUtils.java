package com.rak.dj.djmusicplayer.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rak.dj.djmusicplayer.musiceditmanager.RingDroidActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by sraksh on 2/13/2018.
 */

public class MusicUtils {

    @NonNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File createAlbumArtDir() {
        File albumArtDir = new File(Environment.getExternalStorageDirectory(), "/albumthumbs/");
        if (!albumArtDir.exists()) {
            albumArtDir.mkdirs();
            try {
                new File(albumArtDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return albumArtDir;
    }

    @NonNull
    public static File createAlbumArtFile() {
        return new File(createAlbumArtDir(), String.valueOf(System.currentTimeMillis()));
    }

    public static void insertAlbumArt(@NonNull Context context, int albumId, String path) {
        ContentResolver contentResolver = context.getContentResolver();

        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        contentResolver.delete(ContentUris.withAppendedId(artworkUri, albumId), null, null);

        ContentValues values = new ContentValues();
        values.put("album_id", albumId);
        values.put("_data", path);

        contentResolver.insert(artworkUri, values);
    }

    public static void deleteAlbumArt(@NonNull Context context, int albumId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri localUri = Uri.parse("content://media/external/audio/albumart");
        contentResolver.delete(ContentUris.withAppendedId(localUri, albumId), null, null);
    }

    /*private void onRecord() {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse("record"));
            intent.putExtra("was_get_content_intent", mWasGetContentIntent);
            intent.setClassName( "com.rak.dj.djmusicplayer", "com.rak.dj.djmusicplayer.musiceditmanager.RingDroidActivity");
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        } catch (Exception e) {
            Log.e("Jazz Music Player", e.getMessage());
        }
    }*/

    public static void startRingdroidEditor(@NonNull Context context, String filename) {
        try {
            Intent intent = new Intent(context, RingDroidActivity.class);
            intent.setData(Uri.parse(filename));
            //intent.putExtra("was_get_content_intent", mWasGetContentIntent);
            //intent.setClassName( "com.rak.dj.djmusicplayer", "com.rak.dj.djmusicplayer.musiceditmanager.RingDroidActivity");
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("Jazz Music Player", e.getMessage());
        }
    }
}
