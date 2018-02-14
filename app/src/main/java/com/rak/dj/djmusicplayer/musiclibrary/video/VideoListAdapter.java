package com.rak.dj.djmusicplayer.musiclibrary.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import com.rak.dj.djmusicplayer.models.Video;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.songsmanager.BaseSongAdapter;
import com.rak.dj.djmusicplayer.widgets.BubbleTextGetter;

import java.io.File;
import java.util.List;

/**
 * Created by sraksh on 2/11/2018.
 */

public class VideoListAdapter extends BaseSongAdapter<VideoListAdapter.ItemHolder> implements BubbleTextGetter {

    private List<Video> arraylist;
    private AppCompatActivity mContext;
    private String ateKey;
    private boolean animate;
    private int lastPosition = -1, itemPosition;

    public VideoListAdapter(AppCompatActivity context, List<Video> arraylist, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.ateKey = Helpers.getATEKey(context);
        this.animate = animate;
    }

    @Override
    public VideoListAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, null);
        VideoListAdapter.ItemHolder ml = new VideoListAdapter.ItemHolder(v);
        return ml;
    }


    @Override
    public void onBindViewHolder(VideoListAdapter.ItemHolder itemHolder, int position) {
        this.itemPosition = position;
        Video localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.duration.setText(JazzUtils.getReadableDurationString(localItem.duration));

        ImageLoader.getInstance().displayImage("file://" + localItem.miniThumbMagic,
                itemHolder.videoThumbnail, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

        itemHolder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));

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
        return Character.toString(arraylist.get(pos).title.charAt(0));
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, duration;
        protected ImageView videoThumbnail;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.album_video_title);
            this.duration = (TextView) view.findViewById(R.id.duration);
            this.videoThumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                    intent.setData(Uri.fromFile(new File(arraylist.get(getAdapterPosition()).data)));
                    mContext.startActivity(intent);
                }
            }, 100);


        }
    }
}
