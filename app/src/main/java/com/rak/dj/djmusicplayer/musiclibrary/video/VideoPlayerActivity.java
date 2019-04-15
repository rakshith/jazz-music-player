package com.rak.dj.djmusicplayer.musiclibrary.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.rak.dj.djmusicplayer.R;
import com.videoplayer.JazzVideoCallback;
import com.videoplayer.JazzVideoPlayer;

public class VideoPlayerActivity extends Activity implements JazzVideoCallback {

    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private JazzVideoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Uri playerSource =  getIntent().getData();

        // Grabs a reference to the player view
        player = findViewById(R.id.player);

        // Sets the callback to this Activity, since it inherits EasyVideoCallback
        player.setCallback(this);

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        //player.setSource(Uri.parse(TEST_URL));
        player.setSource(playerSource);

        // From here, the player view will show a progress indicator until the player is prepared.
        // Once it's prepared, the progress indicator goes away and the controls become enabled for the user to begin playback.
    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    // Methods for the implemented EasyVideoCallback

    @Override
    public void onPreparing(JazzVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPrepared(JazzVideoPlayer player) {
        // TODO handle
    }

    @Override
    public void onBuffering(int percent) {
        // TODO handle if needed
    }

    @Override
    public void onError(JazzVideoPlayer player, Exception e) {
        // TODO handle
    }

    @Override
    public void onCompletion(JazzVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onRetry(JazzVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onSubmit(JazzVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onClickVideoFrame(JazzVideoPlayer player) {

    }

    @Override
    public void onStarted(JazzVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPaused(JazzVideoPlayer player) {
        // TODO handle if needed
    }
}
