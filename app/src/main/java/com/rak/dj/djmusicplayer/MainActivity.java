package com.rak.dj.djmusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rak.dj.djmusicplayer.foldermanager.FoldersFragment;
import com.rak.dj.djmusicplayer.helpers.ATEUtils;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.ImageUtils;
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.helpers.NavigationUtils;
import com.rak.dj.djmusicplayer.lyricsmanager.LyricsFragment;
import com.rak.dj.djmusicplayer.musiclibrary.AlbumDetailFragment;
import com.rak.dj.djmusicplayer.musiclibrary.ArtistDetailFragment;
import com.rak.dj.djmusicplayer.musiclibrary.MusicLibraryFragment;
import com.rak.dj.djmusicplayer.musiclibrary.video.VideoListFragment;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.permissions.Nammu;
import com.rak.dj.djmusicplayer.permissions.PermissionCallback;
import com.rak.dj.djmusicplayer.playingmanager.NowPlayingActivity;
import com.rak.dj.djmusicplayer.playingmanager.QuickPlayExpandedFragment;
import com.rak.dj.djmusicplayer.playlistmanager.PlaylistFragment;
import com.rak.dj.djmusicplayer.queuemanager.QueueFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements ATEActivityThemeCustomizer {

    private TextView songtitle, songartist;
    private ImageView albumart;

    private NavigationView navigationView;

    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private DrawerLayout mDrawerLayout;
    private boolean isDarkTheme;
    private String action;
    private Handler navDrawerRunnable = new Handler();
    private Runnable runnable;

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

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }

        addBackstackListener();

        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateMusicLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(Constants.NAVIGATE_QUEUE, navigateQueue);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_LYRICS, navigateLyrics);

        setPanelSlideListeners(slidingUpPanelLayout);
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

        if (JazzUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
        } else {
            loadEverything();
        }

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
        long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
        Fragment fragment = AlbumDetailFragment.newInstance(albumID, false, null);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.songContainer, fragment).commit();
    };

    private Runnable navigateArtist = () -> {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
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
        String ateKey = Helpers.getATEKey(this);
        ATEUtils.setStatusBarColor(this, ateKey, Config.primaryColor(this, ateKey));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
            }, 50);
        }
    }

    private void setDetailsToHeader(){
        String name = MusicPlayer.getTrackName();
        String artist = MusicPlayer.getArtistName();

        if (name != null && artist != null) {
            songtitle.setText(name);
            songartist.setText(artist);
        }
        ImageLoader.getInstance().displayImage(JazzUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
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

            case R.id.nav_nowplaying:
                NavigationUtils.navigateToNowplaying(MainActivity.this, false);
                break;

            case R.id.nav_settings:

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
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.BLACK, MaterialDrawableBuilder.IconValue.LIBRARY_MUSIC));
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.BLACK, MaterialDrawableBuilder.IconValue.BOOKMARK_MUSIC));
            navigationView.getMenu().findItem(R.id.nav_driver_mode).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.BLACK, MaterialDrawableBuilder.IconValue.CAR));
            navigationView.getMenu().findItem(R.id.nav_music_tools).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.BLACK, MaterialDrawableBuilder.IconValue.CONTENT_CUT));

            navigationView.getMenu().findItem(R.id.nav_find_duplicate).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.BLACK, MaterialDrawableBuilder.IconValue.CONTENT_COPY));
            navigationView.getMenu().findItem(R.id.nav_timer).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.BLACK, MaterialDrawableBuilder.IconValue.TIMER));
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);
        } else {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.WHITE, MaterialDrawableBuilder.IconValue.LIBRARY_MUSIC));
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.WHITE, MaterialDrawableBuilder.IconValue.BOOKMARK_MUSIC));
            navigationView.getMenu().findItem(R.id.nav_driver_mode).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.WHITE, MaterialDrawableBuilder.IconValue.CAR));
            navigationView.getMenu().findItem(R.id.nav_music_tools).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.WHITE, MaterialDrawableBuilder.IconValue.CONTENT_CUT));

            navigationView.getMenu().findItem(R.id.nav_find_duplicate).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.WHITE, MaterialDrawableBuilder.IconValue.CONTENT_COPY));
            navigationView.getMenu().findItem(R.id.nav_timer).setIcon(ImageUtils.buildMaterialIcon(getBaseContext(), Color.WHITE, MaterialDrawableBuilder.IconValue.TIMER));
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

    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeNormalDark : R.style.AppThemeNormalLight;
    }

    private void loadEverything(){
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateMusicLibrary.run();
        }
        new initQuickControls().execute("");
    }

    private void checkPermissionAndThenLoad() {
        //check for permission
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(slidingUpPanelLayout, "DJ Music Player will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", view -> Nammu.askForPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback)).show();
            } else {
                Nammu.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback);
            }
        }
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> getSupportFragmentManager().findFragmentById(R.id.songContainer).onResume());
    }
}
