package com.rak.dj.djmusicplayer.models;

/**
 * Created by sraksh on 2/11/2018.
 */

public class AlbumVideo {

    public final long id;
    public final String albumName;
    public final int videoCount;

    public AlbumVideo() {
        this.id=-1;
        this.albumName="";
        this.videoCount=-1;
    }

    public AlbumVideo(long _id, String _albumName, int _videoCount){
        this.id=_id;
        this.albumName=_albumName;
        this.videoCount = _videoCount;
    }

    @Override
    public String toString() {
        return "AlbumVideo{" +
                "id=" + id +
                ", albumName='" + albumName + '\'' +
                ", videoCount=" + videoCount +
                '}';
    }
}
