package com.rak.dj.djmusicplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rak.dj.djmusicplayer.appintro.AppIntroActivity;
import com.rak.dj.djmusicplayer.foldermanager.FoldersFragment;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.lyricsmanager.LyricsFragment;
import com.rak.dj.djmusicplayer.musiclibrary.AlbumDetailFragment;
import com.rak.dj.djmusicplayer.musiclibrary.ArtistDetailFragment;
import com.rak.dj.djmusicplayer.musiclibrary.MusicLibraryFragment;
import com.rak.dj.djmusicplayer.musiclibrary.video.VideoListFragment;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.permissions.JazzPermissionManger;
import com.rak.dj.djmusicplayer.playingmanager.NowPlayingActivity;
import com.rak.dj.djmusicplayer.playingmanager.QuickPlayExpandedFragment;
import com.rak.dj.djmusicplayer.playlistmanager.PlaylistFragment;
import com.rak.dj.djmusicplayer.queuemanager.QueueFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseMainActivity implements ATEActivityThemeCustomizer{

    private TextView songtitle, songartist;
    private ImageView albumart;

    private NavigationView navigationView;

    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private DrawerLayout mDrawerLayout;

    private String action;
    private Handler navDrawerRunnable = new Handler();
    private Runnable runnable;

    private boolean isDarkTheme;

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public View createContentView() {
        View mainContent = getLayoutInflater().inflate(R.layout.activity_main, null);
        return mainContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        action = getIntent().getAction();

        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);

        super.onCreate(savedInstanceState);

        //addBackstackListener();

        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateMusicLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(Constants.NAVIGATE_QUEUE, navigateQueue);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_LYRICS, navigateLyrics);

        //setPanelSlideListeners(slidingUpPanelLayout);
        mDrawerLayout =  findViewById(R.id.drawer_layout);

        navigationView =  findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        albumart =  header.findViewById(R.id.album_art);
        songtitle =  header.findViewById(R.id.song_title);
        songartist =  header.findViewById(R.id.song_artist);

        navDrawerRunnable.postDelayed(() -> {
            setupDrawerContent(navigationView);
            setupNavigationIcons(navigationView);
        }, 700);


        shouldNavigateToAppIntro.run();

        if (slidingUpPanelLayout.getPanelState()!= SlidingUpPanelLayout.PanelState.HIDDEN  && MusicPlayer.getTrackName() == null ) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }

        if(Intent.ACTION_VIEW.equals(action)) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayer.clearQueue();
                MusicPlayer.openFile(getIntent().getData().getPath());
                MusicPlayer.playOrPause();
                navigateNowplaying.run();
            }, 350);
        }
    }

    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeNormalDark : R.style.AppThemeNormalLight;
    }

    private Runnable shouldNavigateToAppIntro =() -> {
        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
        //  If the activity has never started before...
        if (isFirstStart) {
            //  Launch app intro
            final Intent i = new Intent(MainActivity.this, AppIntroActivity.class);
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    startActivity(i);
                }
            });
            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();
            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("firstStart", false);
            //  Apply changes
            e.apply();
        }else{
            runOnUiThread(() -> {
                if (JazzUtil.isMarshmallow()) {
                    checkExternalStoragePermissionAndThenLoad();
                } else {
                    loadOnPermissionGranted();
                }
            });
        }
    };

    /*
    way to safely perform Fragment Transactions after Activity.onSaveInstanceState(…)
     */
    private Runnable navigateMusicLibrary =() -> {
        navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
        Fragment fragment = new MusicLibraryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.songContainer, fragment).commitAllowingStateLoss();
    };

    private Runnable navigatePlaylist = () -> {
        navigationView.getMenu().findItem(R.id.nav_playlists).setChecked(false);
        Fragment fragment = new PlaylistFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.songContainer, fragment).commit();
    };

    private Runnable navigateFolder = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_folders).setChecked(true);
            Fragment fragment = new FoldersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.songContainer));
            transaction.replace(R.id.songContainer, fragment).commit();

        }
    };

    private Runnable navigateQueue = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_queue).setChecked(true);
            Fragment fragment = new QueueFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.songContainer));
            transaction.replace(R.id.songContainer, fragment).commit();

        }
    };

    private Runnable navigateNowplaying = () -> {
        navigateMusicLibrary.run();
        startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
    };

    private Runnable navigateAlbum = () -> {
        int albumID = getIntent().getExtras().getInt(Constants.ALBUM_ID);
        Fragment fragment = AlbumDetailFragment.newInstance(albumID, false, null);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.songContainer, fragment).commit();
    };

    private Runnable navigateArtist = () -> {
        int artistID = getIntent().getExtras().getInt(Constants.ARTIST_ID);
        Fragment fragment = ArtistDetailFragment.newInstance(artistID, false, null);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.songContainer, fragment).commit();
    };

    private Runnable navigateLyrics = () -> {
        Fragment fragment = new LyricsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.songContainer, fragment).commit();
    };


    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.songContainer);
        return (currentFragment instanceof MusicLibraryFragment || currentFragment instanceof QueueFragment
                || currentFragment instanceof PlaylistFragment || currentFragment instanceof FoldersFragment
                || currentFragment instanceof QuickPlayExpandedFragment || currentFragment instanceof VideoListFragment);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (isNavigatingMain()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else
                    super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*String ateKey = Helpers.getATEKey(this);
        ATEUtils.setStatusBarColor(this, ateKey, Config.primaryColor(this, ateKey));*/

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();

        setDetailsToHeader();

        if(MusicPlayer.getTrackName() != null)  {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (slidingUpPanelLayout.getPanelState()== SlidingUpPanelLayout.PanelState.HIDDEN  && MusicPlayer.getTrackName() != null ) {
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }
            }, 200);
        }
    }

    private void setDetailsToHeader(){
        String name = MusicPlayer.getTrackName();
        String artist = MusicPlayer.getArtistName();

        if (name != null && artist != null) {
            songtitle.setText(name);
            songartist.setText(artist);
        }
        ImageLoader.getInstance().displayImage(JazzUtil.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build());
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    updatePosition(menuItem);
                    return true;

                });
    }

    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                runnable = navigateMusicLibrary;

                break;
            case R.id.nav_playlists:
                runnable = navigatePlaylist;

                break;
            case R.id.nav_folders:
                runnable = navigateFolder;

                break;

            case R.id.nav_queue:
                runnable = navigateQueue;
                break;

            case R.id.nav_music_tools:
                NavigationUtil.navigateToMusicTools(MainActivity.this, false);
                break;

            case R.id.nav_nowplaying:
                NavigationUtil.navigateToNowplaying(MainActivity.this, false);
                break;

            case R.id.nav_settings:
                NavigationUtil.navigateToSettings(MainActivity.this);
                break;

            case R.id.nav_about:

                break;
        }

        if (runnable != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(() -> runnable.run(), 350);
        }
    }

    private void setupNavigationIcons(NavigationView navigationView) {

        //material-icon-lib currently doesn't work with navigationview of design support library 22.2.0+
        //set icons manually for now
        //https://github.com/code-mc/material-icon-lib/issues/15

        if (!isDarkTheme) {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.ic_library_music_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.ic_bookmark_music_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_driver_mode).setIcon(R.drawable.ic_directions_car_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_music_tools).setIcon(R.drawable.ic_music_edit_black_24dp);

            navigationView.getMenu().findItem(R.id.nav_find_duplicate).setIcon(R.drawable.ic_find_duplicate_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_timer).setIcon(R.drawable.ic_timelapse_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);
        } else {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.ic_library_music_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.ic_bookmark_music_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_driver_mode).setIcon(R.drawable.ic_directions_car_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_music_tools).setIcon(R.drawable.ic_music_edit_white_24dp);

            navigationView.getMenu().findItem(R.id.nav_find_duplicate).setIcon(R.drawable.ic_find_duplicate_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_timer).setIcon(R.drawable.ic_timelapse_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings_white);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information_white);
        }
    }


    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        JazzPermissionManger.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void loadOnPermissionGranted(){
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateMusicLibrary.run();
        }
        new initQuickControls().execute("");
    }

    private void addBackstackListener() {
        /*getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            getSupportFragmentManager().findFragmentById(R.id.songContainer).onResume();
        });*/
    }

}
