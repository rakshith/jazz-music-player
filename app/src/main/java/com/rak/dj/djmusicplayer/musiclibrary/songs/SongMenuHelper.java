package com.rak.dj.djmusicplayer.musiclibrary.songs;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.MusicUtil;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.playlistmanager.AddPlaylistDialog;

/**
 * Created by sraksh on 2/18/2018.
 */

public class SongMenuHelper {
    public static final int MENU_RES = R.menu.popup_song;

    private static boolean handleMenuClick(AppCompatActivity mContext, int itemId, Song mSong){

        switch (itemId) {
            case R.id.popup_song_play:
                long[] song = new long[1];
                song[0] = mSong.id;
                MusicPlayer.playAll(mContext, song , 0, -1, JazzUtil.IdType.NA, false);
                return true;
            case R.id.popup_song_play_next:
                long[] ids = new long[1];
                ids[0] = mSong.id;
                MusicPlayer.playNext(mContext, ids, -1, JazzUtil.IdType.NA);
                return true;
            case R.id.popup_song_goto_album:
                NavigationUtil.goToAlbum(mContext, mSong.albumId);
                return true;
            case R.id.popup_song_goto_artist:
                NavigationUtil.goToArtist(mContext, mSong.artistId);
                return true;
            case R.id.popup_song_addto_queue:
                long[] id = new long[1];
                id[0] = mSong.id;
                MusicPlayer.addToQueue(mContext, id, -1, JazzUtil.IdType.NA);
                return true;
            case R.id.popup_song_addto_playlist:
                AddPlaylistDialog.newInstance(mSong).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.popup_cut:

                MusicUtil.startRingdroidEditor(mContext.getBaseContext(), mSong.data);
                return true;
            case R.id.popup_tag_editor:
                NavigationUtil.navigateToSongTagEditor(mContext, mSong.id);
                return true;
            case R.id.popup_song_share:
                JazzUtil.shareTrack(mContext, mSong.id);
                return true;
        }
        return false;
    }

    public static abstract class OnClickSongMenu implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private AppCompatActivity activity;

        public OnClickSongMenu(@NonNull AppCompatActivity activity) {
            this.activity = activity;
        }

        public int getMenuRes() {
            return MENU_RES;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(activity, v);
            popupMenu.inflate(getMenuRes());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return handleMenuClick(activity, item.getItemId(), getSong());
        }

        public abstract Song getSong();
    }
}
