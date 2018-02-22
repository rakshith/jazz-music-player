package com.rak.dj.djmusicplayer.musiclibrary.songs;

import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;

import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musiclibrary.AbsRecyclerViewAdapter;
import com.rak.dj.djmusicplayer.musiclibrary.BaseViewHolder;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.widgets.MusicVisualizer;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

/**
 * Created by sraksh on 1/12/2018.
 */

public class SongsAdapter extends AbsRecyclerViewAdapter<Song, SongsAdapter.ItemHolder> implements FastScrollRecyclerView.SectionedAdapter {

    public int currentlyPlayingPosition;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private long playlistId;

    public SongsAdapter(AppCompatActivity context, ArrayList<Song> arraylist, boolean isPlaylistSong, boolean animate) {
        super(context, arraylist);
        this.isPlaylist = isPlaylistSong;
        this.songIDs = getSongIds();
        this.animate = animate;
    }

    @Override
    public String getAteKey() {
        return this.ateKey = Helpers.getATEKey(mContext);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isPlaylist) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_playlist, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    @Override
    public void genericBindViewHolder(ItemHolder itemHolder, int position) {

        Song localItem = items.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);

        ImageLoader.getInstance().displayImage(JazzUtil.getAlbumArtUri(localItem.albumId).toString(),
                itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(Config.accentColor(mContext, ateKey));
            if (MusicPlayer.isPlaying()) {
                itemHolder.visualizer.setColor(Config.accentColor(mContext, ateKey));
                itemHolder.visualizer.setVisibility(View.VISIBLE);
            } else {
                itemHolder.visualizer.setVisibility(View.GONE);
            }
        } else {
            itemHolder.visualizer.setVisibility(View.GONE);
            if (isPlaylist) {
                itemHolder.title.setTextColor(Color.WHITE);
            } else {
                itemHolder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));
            }
        }


        if (animate && isPlaylist) {
            if (JazzUtil.isLollipop())
                setAnimation(itemHolder.itemView, position);
            else {
                if (position > 10)
                    setAnimation(itemHolder.itemView, position);
            }
        }
        setOnPopupMenuListener(itemHolder, position);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.popupMenu.setOnClickListener(v -> {

            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.inflate(R.menu.popup_song);
            menu.setOnMenuItemClickListener(item -> {

                menuFunctionalityForSong(mContext, item, position, playlistId, songIDs);
                return false;
            });

            menu.show();
            if (isPlaylist)
                menu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }



    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = items.get(i).id;
        }

        return ret;
    }

    @Override
    public void updateDataSet(ArrayList<Song> data) {
        this.items = data;
        this.songIDs = getSongIds();
        notifyDataSetChanged();
    }

    public ArrayList<Song> getDataSet() {
        return this.items;
    }

    public int getCurrentlyPlayingPosition() {
        return currentlyPlayingPosition;
    }

    public Song getSongAt(int i) {
        return this.items.get(i);
    }

    public void addSongTo(int i, Song song) {
        this.items.add(i, song);
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        if (this.items == null || this.items.size() == 0)
            return "";
        Character ch = this.items.get(position).title.charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    public class ItemHolder extends BaseViewHolder implements View.OnClickListener {
        protected TextView title, artist;
        protected ImageView albumArt, popupMenu;
        private MusicVisualizer visualizer;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.song_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            visualizer = (MusicVisualizer) view.findViewById(R.id.visualizer);
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
