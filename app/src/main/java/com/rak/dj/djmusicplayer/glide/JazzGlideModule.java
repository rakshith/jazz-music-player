package com.rak.dj.djmusicplayer.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import com.rak.dj.djmusicplayer.glide.artistimage.ArtistImage;
import com.rak.dj.djmusicplayer.glide.artistimage.ArtistImageLoader;
import com.rak.dj.djmusicplayer.glide.audiocover.AudioFileCover;
import com.rak.dj.djmusicplayer.glide.audiocover.AudioFileCoverLoader;


import java.io.InputStream;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class JazzGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());
        glide.register(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory(context));
    }
}
