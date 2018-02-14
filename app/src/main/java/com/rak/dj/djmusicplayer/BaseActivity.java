package com.rak.dj.djmusicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.musiceditmanager.AbsPermissionsActivity;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicService;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicStateListener;
import com.rak.dj.djmusicplayer.playingmanager.QuickPlayExpandedFragment;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.NavigationUtils;
import com.rak.dj.djmusicplayer.playingmanager.QuickPlayFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer.mService;

public abstract class BaseActivity extends AbsPermissionsActivity implements ServiceConnection, MusicStateListener, SlidingUpPanelLayout.PanelSlideListener {




    private QuickPlayFragment quickPlayFragment;
    private QuickPlayExpandedFragment quickPlayExpandedFragment;

    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus;
    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(createContentView());

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        mToken = MusicPlayer.bindToService(this, this);
        mPlaybackStatus = new PlaybackStatus(this);
        //make volume keys change multimedia volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(slidingUpPanelLayout != null) {
            slidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    slidingUpPanelLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        onPanelSlide(slidingUpPanelLayout, 1);
                        onPanelExpanded(slidingUpPanelLayout);
                    } else if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        onPanelCollapsed(slidingUpPanelLayout);
                    } else {
                        //expand_play_fragment.onHide();
                    }
                }
            });
            slidingUpPanelLayout.addPanelSlideListener(this);
        }
    }

    public abstract View createContentView();

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mService = IAppService.Stub.asInterface(service);

    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mService = null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        final IntentFilter filter = new IntentFilter();
        // Play and pause changes
        filter.addAction(MusicService.PLAYSTATE_CHANGED);
        // Track changes
        filter.addAction(MusicService.META_CHANGED);
        // Update a list, probably the playlist fragment's
        filter.addAction(MusicService.REFRESH);
        // If a playlist has changed, notify us
        filter.addAction(MusicService.PLAYLIST_CHANGED);
        // If there is an error playing a track
        filter.addAction(MusicService.TRACK_ERROR);

        registerReceiver(mPlaybackStatus, filter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (!JazzUtils.hasEffectsPanel(BaseActivity.this)) {
            menu.removeItem(R.id.action_equalizer);
        }
        ATE.applyMenu(this, getATEKey(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_settings:
                //NavigationUtils.navigateToSettings(this);
                return true;
            case R.id.action_shuffle:
                Handler handler = new Handler();
                handler.postDelayed(() -> MusicPlayer.shuffleAll(BaseActivity.this), 80);
                return true;
            case R.id.action_search:
                NavigationUtils.navigateToSearch(this);
                return true;
            case R.id.action_equalizer:
                NavigationUtils.navigateToEqualizer(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setPanelSlideListeners(SlidingUpPanelLayout panelLayout) {
        
        panelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        onMetaChanged();
        super.onResume();
    }

    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }

        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
        }
        mMusicStateListener.clear();
    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
        }
    }

    @Override
    public void restartLoader() {
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.restartLoader();
            }
        }

    }

    @Override
    public void onPlaylistChanged() {

        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onPlaylistChanged();
            }
        }
    }

    @Override
    public void onMetaChanged() {

        // Let the listener know to the meta chnaged
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseActivity> mReference;


        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<BaseActivity>(activity);
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            BaseActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(MusicService.META_CHANGED)) {
                    baseActivity.onMetaChanged();
                } else if (action.equals(MusicService.PLAYSTATE_CHANGED)) {
//                    baseActivity.mPlayPauseProgressButton.getPlayPauseButton().updateState();
                } else if (action.equals(MusicService.REFRESH)) {
                    baseActivity.restartLoader();
                } else if (action.equals(MusicService.PLAYLIST_CHANGED)) {
                    baseActivity.onPlaylistChanged();
                } else if (action.equals(MusicService.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(MusicService.TrackErrorExtra.TRACK_NAME));
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class initQuickControls extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            QuickPlayExpandedFragment expandPlayFragment = new QuickPlayExpandedFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.expand_play_fragment, expandPlayFragment).commitAllowingStateLoss();

            QuickPlayFragment fragment1 = new QuickPlayFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.quick_play_fragment, fragment1).commitAllowingStateLoss();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            quickPlayFragment = (QuickPlayFragment) getSupportFragmentManager().findFragmentById(R.id.quick_play_fragment);
            quickPlayExpandedFragment = (QuickPlayExpandedFragment) getSupportFragmentManager().findFragmentById(R.id.expand_play_fragment);
        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onPanelSlide(View panel, @FloatRange(from = 0, to = 1) float slideOffset) {
        setMiniPlayerAlphaProgress(slideOffset);

        setExpandPlayerAlphaProgress(slideOffset);


    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        switch (newState) {
            case COLLAPSED:
                onPanelCollapsed(panel);
                break;
            case EXPANDED:
                onPanelExpanded(panel);
                break;
            case ANCHORED:
                collapsePanel(); // this fixes a bug where the panel would get stuck for some reason
                break;
        }
    }

    private void setExpandPlayerAlphaProgress(@FloatRange(from = 0, to = 1) float progress){
        if (quickPlayExpandedFragment.getView() == null) return;
        float alpha = 0 + progress;
        quickPlayExpandedFragment.getView().setAlpha(alpha);
        // necessary to make the views below clickable
        quickPlayExpandedFragment.getView().setVisibility(alpha == 0 ? View.GONE : View.VISIBLE);
    }

    private void setMiniPlayerAlphaProgress(@FloatRange(from = 0, to = 1) float progress) {
        if (quickPlayFragment.getView() == null) return;
        float alpha = 1 - progress;
        quickPlayFragment.getView().setAlpha(alpha);
        // necessary to make the views below clickable
        quickPlayFragment.getView().setVisibility(alpha == 0 ? View.GONE : View.VISIBLE);
    }

    public void onPanelCollapsed(View panel) {
        // restore values
        //super.setLightStatusbar(lightStatusbar);
        //super.setTaskDescriptionColor(taskColor);
        //super.setNavigationbarColor(navigationbarColor);

        //playerFragment.setMenuVisibility(false);
       // playerFragment.setUserVisibleHint(false);
       // playerFragment.onHide();
        if(quickPlayFragment != null && quickPlayExpandedFragment != null){
            quickPlayFragment.getView().setVisibility(View.VISIBLE);
            quickPlayExpandedFragment.getView().setVisibility(View.GONE);
        }

    }

    public void onPanelExpanded(View panel) {
        // setting fragments values
        //int playerFragmentColor = playerFragment.getPaletteColor();
        //super.setLightStatusbar(false);
        //super.setTaskDescriptionColor(playerFragmentColor);
        //super.setNavigationbarColor(playerFragmentColor);

        //playerFragment.setMenuVisibility(true);
        //playerFragment.setUserVisibleHint(true);
        //playerFragment.onShow();
        if(quickPlayFragment != null && quickPlayExpandedFragment != null) {
            quickPlayFragment.getView().setVisibility(View.GONE);
            quickPlayExpandedFragment.getView().setVisibility(View.VISIBLE);
        }
    }

    public SlidingUpPanelLayout.PanelState getPanelState() {
        return slidingUpPanelLayout == null ? null : slidingUpPanelLayout.getPanelState();
    }

    public void collapsePanel() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void expandPanel() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void hideBottomBar(final boolean hide) {
        if (hide) {
            slidingUpPanelLayout.setPanelHeight(0);
            collapsePanel();
        } else {
            slidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.quick_controls_height));
        }
    }
}
