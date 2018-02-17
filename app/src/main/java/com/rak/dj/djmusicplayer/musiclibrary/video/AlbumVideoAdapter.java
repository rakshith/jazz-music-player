package com.rak.dj.djmusicplayer.musiclibrary.video;

import android.os.Handler;

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
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.helpers.NavigationUtils;
import com.rak.dj.djmusicplayer.models.AlbumVideo;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musiclibrary.BaseAdapter;
import com.rak.dj.djmusicplayer.widgets.BubbleTextGetter;

import java.util.List;

/**
 * Created by sraksh on 2/11/2018.
 */

public class AlbumVideoAdapter extends BaseAdapter<AlbumVideoAdapter.ItemHolder> implements BubbleTextGetter {

    private List<AlbumVideo> arraylist;
    private AppCompatActivity mContext;
    private String ateKey;
    private boolean animate;
    private int lastPosition = -1, itemPosition;

    public AlbumVideoAdapter(AppCompatActivity context, List<AlbumVideo> arraylist, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.ateKey = Helpers.getATEKey(context);
        this.animate = animate;
    }

    @Override
    public AlbumVideoAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_video, null);
        AlbumVideoAdapter.ItemHolder ml = new AlbumVideoAdapter.ItemHolder(v);
        return ml;
    }


    @Override
    public void onBindViewHolder(AlbumVideoAdapter.ItemHolder itemHolder, int position) {
        this.itemPosition = position;
        AlbumVideo localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.albumName);
        itemHolder.videoCount.setText(localItem.videoCount+" Video");

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
        return Character.toString(arraylist.get(pos).albumName.charAt(0));
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, videoCount;
        protected ImageView folderIcon;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.album_video_title);
            this.videoCount = (TextView) view.findViewById(R.id.video_count);
            this.folderIcon = (ImageView) view.findViewById(R.id.folder_icon);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    NavigationUtils.navigateToVideoList(mContext, arraylist.get(getAdapterPosition()).albumName,
                            new Pair<View, String>(folderIcon, "transition_album_art" + getAdapterPosition()));
                }
            }, 100);


        }
    }

}
