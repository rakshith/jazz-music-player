package com.rak.dj.djmusicplayer.dataloaders.upgraded;

import android.content.Context;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.helpers.SortOrder;
import com.rak.dj.djmusicplayer.models.upgraded.Album;
import com.rak.dj.djmusicplayer.models.upgraded.Song;

import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AlbumLoader {

    public static String getSongLoaderSortOrder(Context context) {
        return PreferencesUtils.getInstance(context).getAlbumSortOrder() + ", " + PreferencesUtils.getInstance(context).getAlbumSongSortOrder();
    }

    @NonNull
    public static ArrayList<Album> getAllAlbums(@NonNull final Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        switch(PreferencesUtils.getInstance(context).getAlbumSortOrder()){
            case SortOrder.AlbumSortOrder.ALBUM_YEAR:
                break;
            case SortOrder.AlbumSortOrder.ALBUM_ARTIST:
                break;
            case SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS:
                break;

            default:
                songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        null,
                        null,
                        getSongLoaderSortOrder(context))
                );
        }

        return splitIntoAlbums(songs);
    }

    @NonNull
    public static ArrayList<Album> getAlbums(@NonNull final Context context, String query) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ALBUM + " LIKE ?",
                new String[]{"%" + query + "%"},
                getSongLoaderSortOrder(context))
        );
        return splitIntoAlbums(songs);
    }

    @NonNull
    public static Album getAlbum(@NonNull final Context context, int albumId) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(context, AudioColumns.ALBUM_ID + "=?", new String[]{String.valueOf(albumId)}, getSongLoaderSortOrder(context)));
        return new Album(songs);
    }

    @NonNull
    public static ArrayList<Album> splitIntoAlbums(@Nullable final ArrayList<Song> songs) {
        ArrayList<Album> albums = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                getOrCreateAlbum(albums, song.albumId).songs.add(song);
            }
        }
        return albums;
    }

    private static Album getOrCreateAlbum(ArrayList<Album> albums, int albumId) {
        for (Album album : albums) {
            if (!album.songs.isEmpty() && album.songs.get(0).albumId == albumId) {
                return album;
            }
        }
        Album album = new Album();
        albums.add(album);
        return album;
    }
}
