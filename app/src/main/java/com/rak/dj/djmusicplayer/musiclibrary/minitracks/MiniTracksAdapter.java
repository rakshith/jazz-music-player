package com.rak.dj.djmusicplayer.musiclibrary.minitracks;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.MusicUtil;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musiclibrary.AbsRecyclerViewAdapter;
import com.rak.dj.djmusicplayer.musiclibrary.BaseViewHolder;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musiclibrary.BaseAdapter;
import com.rak.dj.djmusicplayer.widgets.BubbleTextGetter;
import com.rak.dj.djmusicplayer.widgets.PopupImageView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sraksh on 2/12/2018.
 */

public class MiniTracksAdapter extends AbsRecyclerViewAdapter<Song, MiniTracksAdapter.ItemHolder> implements FastScrollRecyclerView.SectionedAdapter {

    public int currentlyPlayingPosition;
    private long[] songIDs;
    private boolean animate;
    private int lastPosition = -1;

    public MiniTracksAdapter(AppCompatActivity context, ArrayList<Song> arraylist, boolean animate) {
        super(context, arraylist);
        this.items = arraylist;
        this.songIDs = getSongIds();
        this.animate = animate;
    }

    @Override
    public String getAteKey() {
        return ateKey = Helpers.getATEKey(mContext);
    }

    @Override
    public MiniTracksAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mini_tracks, null);
        MiniTracksAdapter.ItemHolder ml = new MiniTracksAdapter.ItemHolder(v);
        return ml;
    }

    @Override
    public void genericBindViewHolder(ItemHolder itemHolder, int position) {

        Song localItem = items.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.songArtist.setText(localItem.artistName);
        itemHolder.songDuration.setText(JazzUtil.getReadableDurationString(localItem.duration));

        ImageLoader.getInstance().displayImage(JazzUtil.getAlbumArtUri(localItem.id).toString(),
                itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(Config.accentColor(mContext, ateKey));
        } else {
            itemHolder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));
        }


        if (animate) {
            if (JazzUtil.isLollipop())
                setAnimation(itemHolder.itemView, position);
            else {
                if (position > 10)
                    setAnimation(itemHolder.itemView, position);
            }
        }

        setOnPopupMenuListener(itemHolder, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = items.get(i).id;
        }

        return ret;
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.popupMenu.setOnClickListener(v -> {

            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.inflate(R.menu.popup_song);
            menu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.popup_song_remove_playlist:
                        JazzUtil.removeFromPlaylist(mContext, items.get(position).id, -1);
                        removeSongAt(position);
                        notifyItemRemoved(position);
                        break;
                    case R.id.popup_song_play:
                        MusicPlayer.playAll(mContext,songIDs , position, -1, JazzUtil.IdType.NA, false);
                        break;
                    case R.id.popup_song_play_next:
                        long[] ids = new long[1];
                        ids[0] = items.get(position).id;
                        MusicPlayer.playNext(mContext, ids, -1, JazzUtil.IdType.NA);
                        break;
                    case R.id.popup_song_goto_album:
                        NavigationUtil.goToAlbum(mContext, items.get(position).albumId);
                        break;
                    case R.id.popup_song_goto_artist:
                        NavigationUtil.goToArtist(mContext, items.get(position).artistId);
                        break;
                    case R.id.popup_song_addto_queue:
                        long[] id = new long[1];
                        id[0] = items.get(position).id;
                        MusicPlayer.addToQueue(mContext, id, -1, JazzUtil.IdType.NA);
                        break;
                    case R.id.popup_song_addto_playlist:
                        //AddPlaylistDialog.newInstance(song).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                        break;
                    case R.id.popup_cut:

                        MusicUtil.startRingdroidEditor(mContext.getBaseContext(), items.get(position).data);
                        break;
                    case R.id.popup_tag_editor:
                        NavigationUtil.navigateToSongTagEditor(mContext, items.get(position).id);
                        break;
                    case R.id.popup_song_share:
                        JazzUtil.shareTrack(mContext, items.get(position).id);
                        break;
                    case R.id.popup_song_delete:
                        long[] deleteIds = {items.get(position).id};
                        JazzUtil.showDeleteDialog(mContext,items.get(position).title, deleteIds, MiniTracksAdapter.this, position);
                        break;
                }
                return false;
            });

            menu.show();
        });
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if (items == null || items.size() == 0)
            return "";
        Character ch = items.get(position).title.charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    @Override
    public void updateDataSet(ArrayList<Song> data) {
        this.items = data;
        this.songIDs = getSongIds();
        notifyDataSetChanged();
    }

    public class ItemHolder extends BaseViewHolder implements View.OnClickListener {
        protected TextView title, songArtist, songDuration;
        protected ImageView albumArt;
        protected PopupImageView popupMenu;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.songArtist = (TextView) view.findViewById(R.id.song_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            this.songDuration = (TextView) view.findViewById(R.id.song_duration);
            this.popupMenu = (PopupImageView) view.findViewById(R.id.popup_menu);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentlyPlayingPosition = getAdapterPosition();
                    playAll(mContext, songIDs, getAdapterPosition(), -1,
                            JazzUtil.IdType.NA, false,
                            items.get(getAdapterPosition()), false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(currentlyPlayingPosition);
                            notifyItemChanged(getAdapterPosition());
                        }
                    }, 50);
                }
            }, 100);


        }
    }
}
