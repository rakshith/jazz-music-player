package com.videoplayer;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface IUserMethods {
    void setSource(@NonNull Uri source);

    void setCallback(@NonNull JazzVideoCallback callback);

    void setProgressCallback(@NonNull JazzVideoProgressCallback callback);

    void setLeftAction(@JazzVideoPlayer.LeftAction int action);

    void setRightAction(@JazzVideoPlayer.RightAction int action);

    void setNextDrawable(@NonNull Drawable drawable);

    void setNextDrawableRes(@DrawableRes int res);

    void setRestartDrawable(@NonNull Drawable drawable);

    void setRestartDrawableRes(@DrawableRes int res);

    void setPlayDrawable(@NonNull Drawable drawable);

    void setPlayDrawableRes(@DrawableRes int res);

    void setPauseDrawable(@NonNull Drawable drawable);

    void setPauseDrawableRes(@DrawableRes int res);

    void setThemeColor(@ColorInt int color);

    void setThemeColorRes(@ColorRes int colorRes);

    void setHideControlsOnPlay(boolean hide);

    void setAutoPlay(boolean autoPlay);

    void setInitialPosition(@IntRange(from = 0, to = Integer.MAX_VALUE) int pos);

    void showControls();

    void hideControls();

    @CheckResult
    boolean isControlsShown();

    void toggleControls();

    void enableControls(boolean andShow);

    void disableControls();

    @CheckResult
    boolean isPrepared();

    @CheckResult
    boolean isPlaying();

    @CheckResult
    int getCurrentPosition();

    @CheckResult
    int getDuration();

    void start();

    void seekTo(@IntRange(from = 0, to = Integer.MAX_VALUE) int pos);

    void setVolume(
            @FloatRange(from = 0f, to = 1f) float leftVolume,
            @FloatRange(from = 0f, to = 1f) float rightVolume);

    void pause();

    void stop();

    void reset();

    void release();

    void setAutoFullscreen(boolean autoFullScreen);

    void setLoop(boolean loop);

}
