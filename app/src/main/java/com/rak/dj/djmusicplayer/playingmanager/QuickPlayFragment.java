package com.rak.dj.djmusicplayer.playingmanager;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rak.dj.djmusicplayer.BaseActivity;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.widgets.PlayPauseButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuickPlayFragment extends Fragment implements MusicStateListener  {


    private PlayPauseButton btnPlayPause;
    private ProgressBar mProgress;
    private boolean duetoplaypause = false;
    private boolean fragmentPaused = false;
    private int overflowcounter = 0;
    private ImageView ivSongIcon;
    private TextView tvSongName, tvAlbum;
    private View playPauseWrapper;
    public QuickPlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quick_play, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSongName = (TextView)view.findViewById(R.id.tvSongName);
        tvAlbum = (TextView)view.findViewById(R.id.tvAlbum);
        ivSongIcon = (ImageView)view.findViewById(R.id.ivSongIcon);
        btnPlayPause = (PlayPauseButton) view.findViewById(R.id.play_pause);
        playPauseWrapper = view.findViewById(R.id.play_pause_wrapper);
        playPauseWrapper.setOnClickListener(mPlayPauseListener);

        btnPlayPause.setColor(Config.accentColor(getActivity(), Helpers.getATEKey(getActivity())));

        mProgress = (ProgressBar) view.findViewById(R.id.song_progress_normal);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mProgress.getLayoutParams();
        mProgress.measure(0, 0);
        layoutParams.setMargins(0, -(mProgress.getMeasuredHeight() / 2), 0, 0);
        mProgress.setLayoutParams(layoutParams);


    }

    private void updateNowplayingCard(){
        tvSongName.setText(MusicPlayer.getTrackName());
        tvAlbum.setText(MusicPlayer.getArtistName());
        if (!duetoplaypause) {
            ImageLoader.getInstance().displayImage(JazzUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), ivSongIcon,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .build(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
                            //if (getActivity() != null)
                                //new setBlurredAlbumArt().execute(failedBitmap);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            //if (getActivity() != null)
                                //new setBlurredAlbumArt().execute(loadedImage);

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
        }
        duetoplaypause = false;
        mProgress.setMax((int) MusicPlayer.duration());
        mProgress.postDelayed(mUpdateProgress, 10);
    }

    public void updateState() {
        if (MusicPlayer.isPlaying()) {
            if (!btnPlayPause.isPlayed()) {
                btnPlayPause.setPlayed(true);
                btnPlayPause.startAnimation();
            }
        } else {
            if (btnPlayPause.isPlayed()) {
                btnPlayPause.setPlayed(false);
                btnPlayPause.startAnimation();
            }
        }
    }


    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            mProgress.setProgress((int) position);

            overflowcounter--;
            if (MusicPlayer.isPlaying()) {
                int delay = (int) (1500 - (position % 1000));
                if (overflowcounter < 0 && !fragmentPaused) {
                    overflowcounter++;
                    mProgress.postDelayed(mUpdateProgress, delay);
                }
            } else mProgress.removeCallbacks(this);

        }
    };

    private final View.OnClickListener mPlayPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (!btnPlayPause.isPlayed()) {
                btnPlayPause.setPlayed(true);
                btnPlayPause.startAnimation();
            } else {
                btnPlayPause.setPlayed(false);
                btnPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                }
            }, 200);

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPaused = false;
        if (mProgress != null)
            mProgress.postDelayed(mUpdateProgress, 10);

    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        updateNowplayingCard();
        updateState();
    }

}
