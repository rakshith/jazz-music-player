package com.rak.dj.djmusicplayer.musiclibrary;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.MusicUtil;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.playlistmanager.AddPlaylistDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sraksh on 2/17/2018.
 */

public abstract  class AbsSongsAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected ArrayList<T> items;
    private LayoutInflater layoutInflater;

    /**
     * Base constructor.
     * Allocate adapter-related objects here if needed.
     *
     * @param context Context needed to retrieve LayoutInflater
     */
    public AbsSongsAdapter(AppCompatActivity context, ArrayList<T> dataSet) {
        layoutInflater = LayoutInflater.from(context);
        items = dataSet;
    }

    /**
     * To be implemented in as specific adapter
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the itemView to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        genericBindViewHolder(holder, position);
    }

    public abstract void genericBindViewHolder(VH holder, int position);

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }


    public abstract void updateDataSet(ArrayList<T> arraylist);

    /**
     * Returns an items from the data set at a certain position.
     *
     * @return All of items in this adapter.
     */
    public T getItem(int position) {
        return items.get(position);
    }


    public void removeSongAt(int i){
        items.remove(i);
        updateDataSet(items);
    }

    public void playAll(final Activity context, final long[] list, int position,
                        final long sourceId, final JazzUtil.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {

        MusicPlayer.playAll(context, list, position, -1, JazzUtil.IdType.NA, false);

        if (navigateNowPlaying) {
            NavigationUtil.navigateToNowplaying(context, true);
        }
    }

    protected void menuFunctionalityForSong(AppCompatActivity mContext, MenuItem menuItem, int position, long playlistId, long[] songIDs){
        Song song = (Song) getItem(position);
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
                AddPlaylistDialog.newInstance(song).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
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
                JazzUtil.showDeleteDialog(mContext,song.title, deleteIds, AbsSongsAdapter.this, position);
                break;
        }
    }

}
