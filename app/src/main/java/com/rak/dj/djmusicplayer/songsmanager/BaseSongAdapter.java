package com.rak.dj.djmusicplayer.songsmanager;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.helpers.NavigationUtils;
import com.rak.dj.djmusicplayer.models.Song;

import java.util.List;


/**
 * Created by naman on 7/12/17.
 */

public class BaseSongAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View view) {
            super(view);
        }

    }

    public void playAll(final Activity context, final long[] list, int position,
                        final long sourceId, final JazzUtils.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {

        MusicPlayer.playAll(context, list, position, -1, JazzUtils.IdType.NA, false);

        if (navigateNowPlaying) {
            NavigationUtils.navigateToNowplaying(context, true);
        }
    }

    public void removeSongAt(int i){}
    public void updateDataSet(List<Song> arraylist) {}
}
