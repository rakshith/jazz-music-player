package com.rak.dj.djmusicplayer.musiclibrary;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.MusicUtil;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;

import java.util.List;


/**
 * Created by naman on 7/12/17.
 */

public abstract class BaseAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {


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
                        final long sourceId, final JazzUtil.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {

        MusicPlayer.playAll(context, list, position, -1, JazzUtil.IdType.NA, false);

        if (navigateNowPlaying) {
            NavigationUtil.navigateToNowplaying(context, true);
        }
    }

    protected void menuFunctionalityForSong(AppCompatActivity mContext, MenuItem menuItem, Song song, long playlistId, long[] songIDs){
        int position = getItemPosition();
        switch (menuItem.getItemId()) {
            case R.id.popup_song_remove_playlist:
                JazzUtil.removeFromPlaylist(mContext, song.id, playlistId);
                removeSongAt(position);
                notifyItemRemoved(position);
                break;
            case R.id.popup_song_play:
                MusicPlayer.playAll(mContext,songIDs , position, -1, JazzUtil.IdType.NA, false);
                break;
            case R.id.popup_song_play_next:
                long[] ids = new long[1];
                ids[0] = song.id;
                MusicPlayer.playNext(mContext, ids, -1, JazzUtil.IdType.NA);
                break;
            case R.id.popup_song_goto_album:
                NavigationUtil.goToAlbum(mContext, song.albumId);
                break;
            case R.id.popup_song_goto_artist:
                NavigationUtil.goToArtist(mContext, song.artistId);
                break;
            case R.id.popup_song_addto_queue:
                long[] id = new long[1];
                id[0] = song.id;
                MusicPlayer.addToQueue(mContext, id, -1, JazzUtil.IdType.NA);
                break;
            case R.id.popup_song_addto_playlist:
                //AddPlaylistDialog.newInstance(song).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                break;
            case R.id.popup_cut:

                MusicUtil.startRingdroidEditor(mContext.getBaseContext(), song.data);
                break;
            case R.id.popup_tag_editor:
                NavigationUtil.navigateToSongTagEditor(mContext, song.id);
                break;
            case R.id.popup_song_share:
                JazzUtil.shareTrack(mContext, song.id);
                break;
            case R.id.popup_song_delete:
                long[] deleteIds = {song.id};
                JazzUtil.showDeleteDialog(mContext,song.title, deleteIds, BaseAdapter.this, position);
                break;
        }
    }

    public void removeSongAt(int i){}
    public void updateDataSet(List<Song> arraylist) {}

    public abstract int getItemPosition();
}
