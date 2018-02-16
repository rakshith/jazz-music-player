package com.rak.dj.djmusicplayer.musictoolsmanager;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.rak.dj.djmusicplayer.BaseThemedActivity;
import com.rak.dj.djmusicplayer.R;

public class MusicToolsActivity extends BaseThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_tools);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


}
