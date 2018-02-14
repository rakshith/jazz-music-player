package com.rak.dj.djmusicplayer.musiclibrary.minitracks;

import android.os.Handler;
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
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.models.Song;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.songsmanager.BaseSongAdapter;
import com.rak.dj.djmusicplayer.widgets.BubbleTextGetter;
import com.rak.dj.djmusicplayer.widgets.PopupImageView;

import java.util.List;

/**
 * Created by sraksh on 2/12/2018.
 */

public class MiniTracksAdapter extends BaseSongAdapter<MiniTracksAdapter.ItemHolder> implements BubbleTextGetter {

    public int currentlyPlayingPosition;
    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private String ateKey;
    private boolean animate;
    private int lastPosition = -1, itemPosition;

    public MiniTracksAdapter(AppCompatActivity context, List<Song> arraylist, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.songIDs = getSongIds();
        this.ateKey = Helpers.getATEKey(context);
        this.animate = animate;
    }

    @Override
    public MiniTracksAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mini_tracks, null);
        MiniTracksAdapter.ItemHolder ml = new MiniTracksAdapter.ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(MiniTracksAdapter.ItemHolder itemHolder, int position) {
        this.itemPosition = position;

        Song localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.songArtist.setText(localItem.artistName);
        itemHolder.songDuration.setText(JazzUtils.getReadableDurationString(localItem.duration));

        ImageLoader.getInstance().displayImage(JazzUtils.getAlbumArtUri(localItem.id).toString(),
                itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(Config.accentColor(mContext, ateKey));
        } else {
            itemHolder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));
        }


        if (animate) {
            if (JazzUtils.isLollipop())
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
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.popupMenu.setOnClickListener(v -> {

            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.inflate(R.menu.popup_song);
            menu.setOnMenuItemClickListener(item -> {
                menuFunctionalityForSong(mContext, item, arraylist.get(position), -1, songIDs);
                return false;
            });

            menu.show();
        });
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    @Override
    public int getItemPosition() {
        return itemPosition;
    }

    @Override
    public String getTextToShowInBubble(final int pos) {
        if (arraylist == null || arraylist.size() == 0)
            return "";
        Character ch = arraylist.get(pos).title.charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
                            JazzUtils.IdType.NA, false,
                            arraylist.get(getAdapterPosition()), false);
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
