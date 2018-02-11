package com.rak.dj.djmusicplayer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sraksh on 1/23/2018.
 */

public class MusicLibrary {

    private MusicType musicType;
    private List<Song> songList;
    private List<Album> albumList;
    private List<Artist> artistList;

    public enum MusicType {
        SONG, ARTIST, ALBUM, GENRE, TOP_TRACKS
    }

    public MusicLibrary(){
        this.musicType = MusicType.SONG;
        this.songList = new ArrayList<>();
        this.artistList = new ArrayList<>();
        this.albumList = new ArrayList<>();
    }

    public MusicType getMusicType() {
        return musicType;
    }

    public void setMusicType(MusicType musicType) {
        this.musicType = musicType;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<Artist> artistList) {
        this.artistList = artistList;
    }
}
