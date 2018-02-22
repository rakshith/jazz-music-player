package com.rak.dj.djmusicplayer.dataloaders.upgraded;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rak.dj.djmusicplayer.dataloaders.BaseLoader;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.models.upgraded.Genre;
import com.rak.dj.djmusicplayer.models.upgraded.Song;

import java.util.ArrayList;

/**
 * Created by sraksh on 2/11/2018.
 */

public class GenreLoader extends BaseLoader {

    @NonNull
    public static ArrayList<Genre> getAllGenres(@NonNull final Context context) {
        return getGenresFromCursor(context, makeGenreCursor(context));
    }

    @NonNull
    public static ArrayList<Song> getSongs(@NonNull final Context context, final int genreId) {
        return SongLoader.getSongs(makeGenreSongCursor(context, genreId));
    }


    @NonNull
    private static ArrayList<Genre> getGenresFromCursor(@NonNull final Context context, @Nullable final Cursor cursor) {
        final ArrayList<Genre> genres = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Genre genre = getGenreFromCursor(context, cursor);
                    if (genre.songCount > 0) {
                        genres.add(genre);
                    } else {
                        // try to remove the empty genre from the media store
                        try {
                            context.getContentResolver().delete(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, MediaStore.Audio.Genres._ID + " == " + genre.id, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // nothing we can do then
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return genres;
    }

    @NonNull
    private static Genre getGenreFromCursor(@NonNull final Context context, @NonNull final Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        final int songs = getSongs(context, id).size();
        return new Genre(id, name, songs);
    }

    @Nullable
    private static Cursor makeGenreSongCursor(@NonNull final Context context, int genreId) {
        try {
            return context.getContentResolver().query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                    SongLoader.BASE_PROJECTION, SongLoader.BASE_SELECTION, null, PreferencesUtils.getInstance(context).getSongSortOrder());
        } catch (SecurityException e) {
            return null;
        }
    }

    @Nullable
    private static Cursor makeGenreCursor(@NonNull final Context context) {
        final String[] projection = new String[]{
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
        };

        try {
            return context.getContentResolver().query(
                    MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    projection, null, null, PreferencesUtils.getInstance(context).getGenreSortOrder());
        } catch (SecurityException e) {
            return null;
        }
    }
}
