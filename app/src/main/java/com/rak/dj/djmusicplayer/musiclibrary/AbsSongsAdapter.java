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

import java.util.List;

/**
 * Created by sraksh on 2/17/2018.
 */

public abstract  class AbsSongsAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> items;
    private LayoutInflater layoutInflater;

    /**
     * Base constructor.
     * Allocate adapter-related objects here if needed.
     *
     * @param context Context needed to retrieve LayoutInflater
     */
    public AbsSongsAdapter(AppCompatActivity context, List<T> dataSet) {
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

    /**
     * Sets items to the adapter and notifies that data set has been changed.
     *
     * @param items items to set to the adapter
     * @throws IllegalArgumentException in case of setting `null` items
     */
    public void setItems(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void updateDataSet(List<T> arraylist) {}

    /**
     * Returns all items from the data set held by the adapter.
     *
     * @return All of items in this adapter.
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * Returns an items from the data set at a certain position.
     *
     * @return All of items in this adapter.
     */
    public T getItem(int position) {
        return items.get(position);
    }

    /**
     * Adds item to the end of the data set.
     * Notifies that item has been inserted.
     *
     * @param item item which has to be added to the adapter.
     */
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item to the Recycler adapter");
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Adds list of items to the end of the adapter's data set.
     * Notifies that item has been inserted.
     *
     * @param items items which has to be added to the adapter.
     */
    public void addAll(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot add `null` items to the Recycler adapter");
        }
        this.items.addAll(items);
        notifyItemRangeInserted(this.items.size() - items.size(), items.size());
    }

    /**
     * Clears all the items in the adapter.
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    /**
     * Removes an item from the adapter.
     * Notifies that item has been removed.
     *
     * @param item to be removed
     */
    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Returns whether adapter is empty or not.
     *
     * @return `true` if adapter is empty or `false` otherwise
     */
    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /**
     * Indicates whether each item in the data set can be represented with a unique identifier
     * of type {@link Long}.
     *
     * @param hasStableIds Whether items in data set have unique identifiers or not.
     * @see #hasStableIds()
     * @see #getItemId(int)
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    /**
     * Inflates a view.
     *
     * @param layout       layout to me inflater
     * @param parent       container where to inflate
     * @param attachToRoot whether to attach to root or not
     * @return inflated View
     */
    @NonNull
    protected View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent, final boolean attachToRoot) {
        return layoutInflater.inflate(layout, parent, attachToRoot);
    }

    /**
     * Inflates a view.
     *
     * @param layout layout to me inflater
     * @param parent container where to inflate
     * @return inflated View
     */
    @NonNull
    protected View inflate(@LayoutRes final int layout, final @Nullable ViewGroup parent) {
        return inflate(layout, parent, false);
    }

    public void removeSongAt(int i){}
    public abstract int getItemPosition();

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
