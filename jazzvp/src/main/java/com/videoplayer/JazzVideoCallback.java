package com.videoplayer;

import android.net.Uri;

public interface JazzVideoCallback {
    void onStarted(JazzVideoPlayer player);

    void onPaused(JazzVideoPlayer player);

    void onPreparing(JazzVideoPlayer player);

    void onPrepared(JazzVideoPlayer player);

    void onBuffering(int percent);

    void onError(JazzVideoPlayer player, Exception e);

    void onCompletion(JazzVideoPlayer player);

    void onRetry(JazzVideoPlayer player, Uri source);

    void onNext(JazzVideoPlayer player, Uri source);

    void onClickVideoFrame(JazzVideoPlayer player);
}
