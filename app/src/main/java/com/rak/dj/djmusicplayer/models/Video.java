package com.rak.dj.djmusicplayer.models;

/**
 * Created by sraksh on 2/11/2018.
 */

public class Video {
    public final long id;
    public final String title;
    public final int duration;
    public final String albumName;
    public final String artistName;
    public final String data;
    public final String miniThumbMagic;
    public final String dateTaken;
    public final String dateAdded;
    public final String dateModified;

    public Video() {
        this.id=-1;
        this.title="";
        this.duration=-1;
        this.albumName="";
        this.artistName="";
        this.data="";
        this.miniThumbMagic="";
        this.dateTaken="";
        this.dateAdded="";
        this.dateModified="";
    }

    public Video(long _id, String _title, int _duration, String _albumName, String _artistName, String _data, String _dateTaken, String _miniThumbMagic, String _dateAdded, String _dateModified){
        this.id=_id;
        this.title=_title;
        this.duration=_duration;
        this.albumName=_albumName;
        this.artistName=_artistName;
        this.data = _data;
        this.dateTaken = _dateTaken;
        this.miniThumbMagic = _miniThumbMagic;
        this.dateAdded = _dateAdded;
        this.dateModified = _dateModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Video video = (Video) o;

        return albumName.equals(video.albumName);
    }

    @Override
    public int hashCode() {
        return albumName.hashCode();
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", albumName='" + albumName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", data='" + data + '\'' +
                ", miniTumbMagic=" + miniThumbMagic +
                ", dateTaken='" + dateTaken + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                ", dateModified='" + dateModified + '\'' +
                '}';
    }
}
