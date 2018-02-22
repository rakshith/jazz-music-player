package com.rak.dj.djmusicplayer.musiclibrary.genres;

import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
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
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Genre;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musiclibrary.BaseAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

/**
 * Created by sraksh on 2/11/2018.
 */

public class GenreAdapter extends BaseAdapter<GenreAdapter.ItemHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<Genre> dataSet;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private String ateKey;
    private boolean animate;
    private int lastPosition = -1, itemPosition;

    public GenreAdapter(AppCompatActivity context, ArrayList<Genre> arraylist, boolean animate) {
        this.dataSet = arraylist;
        this.mContext = context;
        this.songIDs = getSongIds();
        this.ateKey = Helpers.getATEKey(context);
        this.animate = animate;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_genre, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int position) {
        this.itemPosition = position;
        Genre localItem = dataSet.get(position);

        itemHolder.title.setText(localItem.name);
        itemHolder.songCount.setText(localItem.songCount+" Songs");

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
            ret[i] = dataSet.get(i).id;
        }

        return ret;
    }

    @Override
    public int getItemCount() {
        return (null != dataSet ? dataSet.size() : 0);
    }

    @Override
    public int getItemPosition() {
        return itemPosition;
    }

    public void updateDataSet(ArrayList<Genre> data) {
        this.dataSet = data;
        notifyDataSetChanged();
    }

    public ArrayList<Genre> getDataSet() {
        return dataSet;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if (dataSet == null || dataSet.size() == 0)
            return "";
        Character ch = dataSet.get(position).name.charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, songCount;
        protected ImageView albumArt;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.songCount = (TextView) view.findViewById(R.id.song_count);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    NavigationUtil.navigateToGenre(mContext, dataSet.get(getAdapterPosition()),
                            new Pair<View, String>(albumArt, "transition_album_art" + getAdapterPosition()));
                }
            }, 100);


        }
    }
}
