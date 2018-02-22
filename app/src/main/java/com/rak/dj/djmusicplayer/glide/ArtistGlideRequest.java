package com.rak.dj.djmusicplayer.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.rak.dj.djmusicplayer.JazzMusicPlayerApp;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.glide.artistimage.ArtistImage;
import com.rak.dj.djmusicplayer.glide.palette.BitmapPaletteTranscoder;
import com.rak.dj.djmusicplayer.glide.palette.BitmapPaletteWrapper;
import com.rak.dj.djmusicplayer.helpers.ArtistSignatureUtil;
import com.rak.dj.djmusicplayer.helpers.CustomArtistImageUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Artist;


/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ArtistGlideRequest {

    private static final DiskCacheStrategy DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.SOURCE;
    private static final int DEFAULT_ERROR_IMAGE = R.drawable.ic_empty_music2;
    public static final int DEFAULT_ANIMATION = android.R.anim.fade_in;

    public static class Builder {
        final RequestManager requestManager;
        final Artist artist;
        boolean noCustomImage;
        boolean forceDownload;

        public static Builder from(@NonNull RequestManager requestManager, Artist artist) {
            return new Builder(requestManager, artist);
        }

        private Builder(@NonNull RequestManager requestManager, Artist artist) {
            this.requestManager = requestManager;
            this.artist = artist;
        }

        public PaletteBuilder generatePalette(Context context) {
            return new PaletteBuilder(this, context);
        }

        public BitmapBuilder asBitmap() {
            return new BitmapBuilder(this);
        }

        public Builder noCustomImage(boolean noCustomImage) {
            this.noCustomImage = noCustomImage;
            return this;
        }

        public Builder forceDownload(boolean forceDownload) {
            this.forceDownload = forceDownload;
            return this;
        }

        public DrawableRequestBuilder<GlideDrawable> build() {
            //noinspection unchecked
            return createBaseRequest(requestManager, artist, noCustomImage, forceDownload)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION)
                    .priority(Priority.LOW)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(artist));
        }
    }

    public static class BitmapBuilder {
        private final Builder builder;

        public BitmapBuilder(Builder builder) {
            this.builder = builder;
        }

        public BitmapRequestBuilder<?, Bitmap> build() {
            //noinspection unchecked
            return createBaseRequest(builder.requestManager, builder.artist, builder.noCustomImage, builder.forceDownload)
                    .asBitmap()
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION)
                    .priority(Priority.LOW)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(builder.artist));
        }
    }

    public static class PaletteBuilder {
        final Context context;
        private final Builder builder;

        public PaletteBuilder(Builder builder, Context context) {
            this.builder = builder;
            this.context = context;
        }

        public BitmapRequestBuilder<?, BitmapPaletteWrapper> build() {
            //noinspection unchecked
            return createBaseRequest(builder.requestManager, builder.artist, builder.noCustomImage, builder.forceDownload)
                    .asBitmap()
                    .transcode(new BitmapPaletteTranscoder(context), BitmapPaletteWrapper.class)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION)
                    .priority(Priority.LOW)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(builder.artist));
        }
    }

    public static DrawableTypeRequest createBaseRequest(RequestManager requestManager, Artist artist, boolean noCustomImage, boolean forceDownload) {
        boolean hasCustomImage = CustomArtistImageUtil.getInstance(JazzMusicPlayerApp.getInstance()).hasCustomArtistImage(artist);
        if (noCustomImage || !hasCustomImage) {
            return requestManager.load(new ArtistImage(artist.getName(), forceDownload));
        } else {
            return requestManager.load(CustomArtistImageUtil.getFile(artist));
        }
    }

    public static Key createSignature(Artist artist) {
        return ArtistSignatureUtil.getInstance(JazzMusicPlayerApp.getInstance()).getArtistSignature(artist.getName());
    }
}