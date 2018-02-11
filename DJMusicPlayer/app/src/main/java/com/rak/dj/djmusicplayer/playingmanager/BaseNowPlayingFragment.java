package com.rak.dj.djmusicplayer.playingmanager;



import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rak.dj.djmusicplayer.BaseActivity;
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicService;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.NavigationUtils;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtility;
import com.rak.dj.djmusicplayer.helpers.SlideTrackSwitcher;
import com.rak.dj.djmusicplayer.widgets.PlayPauseButton;
import com.rak.dj.djmusicplayer.widgets.PlayPauseDrawable;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseNowPlayingFragment extends Fragment implements MusicStateListener{

    TextView songtitle, songalbum, songartist, songDuration, songElapsedTime, tvSongName, tvAlbum;
    SeekBar mProgress;
    boolean fragmentPaused = false;
    int overflowcounter = 0;
    MaterialIconView btnPlayPrev, btnPlayNext;
    FloatingActionButton playPauseFloating;
    PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    private View playPauseWrapper;
    String ateKey;
    public int accentColor;
    public ImageView albumart, shuffle, repeat, ivSongIcon;
    private PlayPauseButton mPlayPause;
    protected boolean menuEnabled = false;
    public BaseNowPlayingFragment() {
        // Required empty public constructor
    }

    //seekbar
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            if (mProgress != null) {
                mProgress.setProgress((int) position);
                if (songElapsedTime != null && getActivity() != null)
                    songElapsedTime.setText(JazzUtils.makeShortTimeString(getActivity(), position / 1000));
            }
            overflowcounter--;
            if (MusicPlayer.isPlaying()) {
                int delay = (int) (1500 - (position % 1000));
                if (overflowcounter < 0 && !fragmentPaused) {
                    overflowcounter++;
                    mProgress.postDelayed(mUpdateProgress, delay);
                }
            }

        }
    };

    public void setMusicStateListener() {
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {

        updateSongDetails();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ateKey = Helpers.getATEKey(getActivity());
        accentColor = Config.accentColor(getActivity(), ateKey);
    }

    protected void initGestures(View v) {
        if (PreferencesUtility.getInstance(v.getContext()).isGesturesEnabled()) {
            new SlideTrackSwitcher() {
                @Override
                public void onSwipeBottom() {
                    getActivity().finish();
                }
            }.attach(v);
        }
    }

    protected void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
    }

    protected void setSongDetail(View view){
        ivSongIcon = (ImageView)view.findViewById(R.id.ivSongIcon);
        albumart = (ImageView) view.findViewById(R.id.album_art);
        shuffle = (ImageView) view.findViewById(R.id.shuffle);
        repeat = (ImageView) view.findViewById(R.id.repeat);

        btnPlayPrev = (MaterialIconView)view.findViewById(R.id.previous);
        btnPlayNext = (MaterialIconView)view.findViewById(R.id.next);
        mPlayPause = (PlayPauseButton) view.findViewById(R.id.playpause);
        playPauseWrapper = view.findViewById(R.id.playpausewrapper);
        songtitle = (TextView) view.findViewById(R.id.song_title);
        tvSongName = (TextView) view.findViewById(R.id.tvSongName);
        songalbum = (TextView) view.findViewById(R.id.song_album);
        tvAlbum = (TextView) view.findViewById(R.id.tvAlbum);
        songartist = (TextView) view.findViewById(R.id.song_artist);

        /*playPauseFloating = (FloatingActionButton)view.findViewById(R.id.playpause);
        if (playPauseFloating != null) {
            playPauseDrawable.setColorFilter(JazzUtils.getBlackWhiteColor(accentColor), PorterDuff.Mode.MULTIPLY);
            playPauseFloating.setImageDrawable(playPauseDrawable);
            if (MusicPlayer.isPlaying())
                playPauseDrawable.transformToPause(false);
            else playPauseDrawable.transformToPlay(false);
        }*/

        if (mPlayPause != null && getActivity() != null) {
            mPlayPause.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }

        songElapsedTime = (TextView)view.findViewById(R.id.song_elapsed_time);
        songDuration = (TextView)view.findViewById(R.id.song_duration);
        mProgress = (SeekBar)view.findViewById(R.id.song_progress);

        setSeekBarListener();

        updateSongDetails();
    }

    public void doAlbumArtStuff(Bitmap loadedImage) {

    }

    private void updateSongDetails() {

        updateShuffleState();
        updateRepeatState();

        if (!duetoplaypause) {
            if (albumart != null) {
                ImageLoader.getInstance().displayImage(JazzUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .showImageOnFail(R.drawable.ic_empty_music2)
                                .build(), new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                doAlbumArtStuff(loadedImage);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
                                doAlbumArtStuff(failedBitmap);
                            }

                        });
            }

            if(ivSongIcon != null){
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

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
            }
        }
        duetoplaypause = false;

        if (btnPlayNext != null) {
            btnPlayNext.setOnClickListener(view -> {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.next();
                        //notifyPlayingDrawableChange();
                    }
                }, 200);

            });
        }
        if (btnPlayPrev != null) {
            btnPlayPrev.setOnClickListener(view -> {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    MusicPlayer.previous(getActivity(), false);
                    //notifyPlayingDrawableChange();
                }, 200);

            });
        }

        if (mPlayPause != null)
            updatePlayPauseButton();

        if (playPauseFloating != null)
            updatePlayPauseFloatingButton();

        if (songtitle != null)
            songtitle.setText(MusicPlayer.getTrackName());

        if (songalbum != null)
            songalbum.setText(MusicPlayer.getAlbumName());

        if(tvSongName !=null) {
            tvSongName.setText(MusicPlayer.getTrackName());
        }

        if(tvAlbum!=null) {
            tvAlbum.setText(MusicPlayer.getArtistName());
        }

        if (songartist != null) {
            songartist.setText(MusicPlayer.getArtistName());
            songartist.setOnClickListener(v -> NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId()));
        }

        if (songDuration != null && getActivity() != null)
            songDuration.setText(JazzUtils.makeShortTimeString(getActivity(), MusicPlayer.duration() / 1000));

        if (playPauseWrapper != null)
            playPauseWrapper.setOnClickListener(mButtonListener);

        if (playPauseFloating != null)
            playPauseFloating.setOnClickListener(mFLoatingButtonListener);

        if (mProgress != null) {
            mProgress.setMax((int) MusicPlayer.duration());
            if (mUpdateProgress != null) {
                mProgress.removeCallbacks(mUpdateProgress);
            }
            mProgress.postDelayed(mUpdateProgress, 10);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(menuEnabled);
    }

    private void shouldEnableMenu(boolean isMenuVisible){
        this.menuEnabled = isMenuVisible;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.now_playing, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_go_to_album:
                NavigationUtils.goToAlbum(getContext(), MusicPlayer.getCurrentAlbumId());
                break;
            case R.id.menu_go_to_artist:
                NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                break;
            case R.id.action_lyrics:
                NavigationUtils.goToLyrics(getContext());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            } else {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                    //if (recyclerView != null && recyclerView.getAdapter() != null)
                        //recyclerView.getAdapter().notifyDataSetChanged();
                }
            }, 200);


        }
    };

    public void notifyPlayingDrawableChange() {
        //int position = MusicPlayer.getQueuePosition();
        //BaseQueueAdapter.currentlyPlayingPosition = position;
    }

    private boolean duetoplaypause = false;
    private final View.OnClickListener mFLoatingButtonListener = v -> {
        duetoplaypause = true;
        playPauseDrawable.transformToPlay(true);
        playPauseDrawable.transformToPause(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MusicPlayer.playOrPause();
                //if (recyclerView != null && recyclerView.getAdapter() != null)
                    //recyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 250);


    };

    public void updatePlayPauseButton() {
        if (MusicPlayer.isPlaying()) {
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
        } else {
            if (mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
        }
    }

    public void updatePlayPauseFloatingButton() {
        if (MusicPlayer.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);
        }
    }

    private void setSeekBarListener() {
        if (mProgress != null)
            mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        MusicPlayer.seek((long) i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
    }

    public void updateShuffleState() {
        if (shuffle != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                    .setSizeDp(30);

            if (getActivity() != null) {
                if (MusicPlayer.getShuffleMode() == 0) {
                    builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                } else builder.setColor(Config.accentColor(getActivity(), ateKey));
            }

            shuffle.setImageDrawable(builder.build());
            shuffle.setOnClickListener(view -> {
                MusicPlayer.cycleShuffle();
                updateShuffleState();
                updateRepeatState();
            });
        }
    }

    public void updateRepeatState() {
        if (repeat != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setSizeDp(30);

            if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_NONE) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_CURRENT) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
                builder.setColor(Config.accentColor(getActivity(), ateKey));
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_ALL) {
                builder.setColor(Config.accentColor(getActivity(), ateKey));
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
            }


            repeat.setImageDrawable(builder.build());
            repeat.setOnClickListener(view -> {
                MusicPlayer.cycleRepeat();
                updateRepeatState();
                updateShuffleState();
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        fragmentPaused = false;
        if (mProgress != null)
            mProgress.postDelayed(mUpdateProgress, 10);
    }

}
