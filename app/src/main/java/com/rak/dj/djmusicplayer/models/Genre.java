package com.rak.dj.djmusicplayer.models;

/**
 * Created by sraksh on 2/11/2018.
 */

public class Genre {
    public final int id;
    public final String name;
    public final int songCount;

    public Genre(){
        this.id = -1;
        this.name="";
        this.songCount=-1;
    }

    public Genre(int id, String name, int songCount) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }
}
