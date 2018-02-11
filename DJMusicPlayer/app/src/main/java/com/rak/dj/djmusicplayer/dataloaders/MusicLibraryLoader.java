package com.rak.dj.djmusicplayer.dataloaders;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.rak.dj.djmusicplayer.models.MusicLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sraksh on 1/24/2018.
 */

public class MusicLibraryLoader {


    public static List<MusicLibrary> buildMusicLibraryList(Context mContext){
        List<MusicLibrary> musicLibraryList = new ArrayList<>();

        MusicLibrary songMusicLibrary = new MusicLibrary();
        songMusicLibrary.setMusicType(MusicLibrary.MusicType.SONG);
        songMusicLibrary.setSongList(SongLoader.getAllSongs(mContext));
        musicLibraryList.add(songMusicLibrary);

        MusicLibrary artistMusicLibrary = new MusicLibrary();
        artistMusicLibrary.setMusicType(MusicLibrary.MusicType.ARTIST);
        artistMusicLibrary.setArtistList(ArtistLoader.getAllArtists(mContext));
        musicLibraryList.add(artistMusicLibrary);

        MusicLibrary albumMusicLibrary = new MusicLibrary();
        albumMusicLibrary.setMusicType(MusicLibrary.MusicType.ALBUM);
        albumMusicLibrary.setAlbumList(AlbumLoader.getAllAlbums(mContext));
        musicLibraryList.add(albumMusicLibrary);

        return musicLibraryList;
    }

}
