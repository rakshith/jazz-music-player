package com.rak.dj.djmusicplayer.tageditormanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.appthemeengine.util.TintHelper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.helpers.MusicUtils;
import com.rak.dj.djmusicplayer.helpers.misc.DialogAsyncTask;
import com.rak.dj.djmusicplayer.helpers.misc.SimpleObservableScrollViewCallbacks;
import com.rak.dj.djmusicplayer.helpers.misc.UpdateToastMediaScannerCompletionListener;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by sraksh on 2/12/2018.
 */

public abstract class AbsTagEditorActivity extends AppCompatActivity {

    private static final String TAG = AbsTagEditorActivity.class.getSimpleName();
    private long id;
    public static final String EXTRA_ID = "extra_id";
    private List<String> songPaths;

    private boolean isInNoImageMode;
    private int headerVariableSpace;
    private int paletteColorPrimary;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1000;
    LinearLayout header;
    FloatingActionButton fab;
    ObservableScrollView observableScrollView;
    Toolbar toolbar;
    ImageView image;

    private final SimpleObservableScrollViewCallbacks observableScrollViewCallbacks = new SimpleObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean b, boolean b2) {
            float alpha;
            if (!isInNoImageMode) {
                alpha = 1 - (float) Math.max(0, headerVariableSpace - scrollY) / headerVariableSpace;
            } else {
                header.setTranslationY(scrollY);
                alpha = 1;
            }
            //toolbar.setBackgroundColor(ColorUtil.withAlpha(paletteColorPrimary, alpha));
            image.setTranslationY(scrollY / 2);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayout());

        getIntentExtras();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        image = findViewById(R.id.image);
        header = findViewById(R.id.header);
        fab = findViewById(R.id.fab);
        observableScrollView = findViewById(R.id.observableScrollView);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.tag_editor);

        songPaths = getSongPaths();
        if (songPaths.isEmpty()) {
            finish();
            return;
        }

        headerVariableSpace = getResources().getDimensionPixelSize(R.dimen.tagEditorHeaderVariableSpace);

        setUpViews();
    }

    private void setUpViews() {
        setUpScrollView();
        setUpFab();
        //setUpImageView();
    }

    private void setUpFab() {
        fab.setScaleX(0);
        fab.setScaleY(0);
        fab.setEnabled(false);
        fab.setOnClickListener(v -> save());

        //TintHelper.setTintAuto(fab, ThemeStore.accentColor(this), true);
    }

    private void setUpScrollView() {
        observableScrollView.setScrollViewCallbacks(observableScrollViewCallbacks);
    }

    protected void writeValuesToFiles(@NonNull final Map<FieldKey, String> fieldKeyValueMap, @Nullable final ArtworkInfo artworkInfo) {
        JazzUtils.hideSoftKeyboard(this);

        new WriteTagsAsyncTask(this).execute(new WriteTagsAsyncTask.LoadingInfo(getSongPaths(), fieldKeyValueMap, artworkInfo));
    }

    private static class WriteTagsAsyncTask extends DialogAsyncTask<WriteTagsAsyncTask.LoadingInfo, Integer, String[]> {
        Context applicationContext;

        public WriteTagsAsyncTask(Context context) {
            super(context);
            applicationContext = context;
        }

        @Override
        protected String[] doInBackground(LoadingInfo... params) {
            try {
                LoadingInfo info = params[0];

                Artwork artwork = null;
                File albumArtFile = null;
                if (info.artworkInfo != null && info.artworkInfo.artwork != null) {
                    try {
                        albumArtFile = MusicUtils.createAlbumArtFile().getCanonicalFile();
                        info.artworkInfo.artwork.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(albumArtFile));
                        artwork = ArtworkFactory.createArtworkFromFile(albumArtFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                int counter = 0;
                boolean wroteArtwork = false;
                boolean deletedArtwork = false;
                for (String filePath : info.filePaths) {
                    publishProgress(++counter, info.filePaths.size());
                    try {
                        AudioFile audioFile = AudioFileIO.read(new File(filePath));
                        Tag tag = audioFile.getTagOrCreateAndSetDefault();

                        if (info.fieldKeyValueMap != null) {
                            for (Map.Entry<FieldKey, String> entry : info.fieldKeyValueMap.entrySet()) {
                                try {
                                    tag.setField(entry.getKey(), entry.getValue());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (info.artworkInfo != null) {
                            if (info.artworkInfo.artwork == null) {
                                tag.deleteArtworkField();
                                deletedArtwork = true;
                            } else if (artwork != null) {
                                tag.deleteArtworkField();
                                tag.setField(artwork);
                                wroteArtwork = true;
                            }
                        }

                        audioFile.commit();
                    } catch (@NonNull CannotReadException | IOException | CannotWriteException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                        e.printStackTrace();
                    }
                }

                Context context = getContext();
                if (context != null) {
                    if (wroteArtwork) {
                        MusicUtils.insertAlbumArt(context, info.artworkInfo.albumId, albumArtFile.getPath());
                    } else if (deletedArtwork) {
                        MusicUtils.deleteAlbumArt(context, info.artworkInfo.albumId);
                    }
                }

                return info.filePaths.toArray(new String[info.filePaths.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] toBeScanned) {
            super.onPostExecute(toBeScanned);
            scan(toBeScanned);
        }

        @Override
        protected void onCancelled(String[] toBeScanned) {
            super.onCancelled(toBeScanned);
            scan(toBeScanned);
        }

        private void scan(String[] toBeScanned) {
            Context context = getContext();
            MediaScannerConnection.scanFile(applicationContext, toBeScanned, null, context instanceof Activity ? new UpdateToastMediaScannerCompletionListener((Activity) context, toBeScanned) : null);
        }

        @Override
        protected Dialog createDialog(@NonNull Context context) {
            return new MaterialDialog.Builder(context)
                    .title(R.string.saving_changes)
                    .cancelable(false)
                    .progress(false, 0)
                    .build();
        }

        @Override
        protected void onProgressUpdate(@NonNull Dialog dialog, Integer... values) {
            super.onProgressUpdate(dialog, values);
            ((MaterialDialog) dialog).setMaxProgress(values[1]);
            ((MaterialDialog) dialog).setProgress(values[0]);
        }

        public static class LoadingInfo {
            public final Collection<String> filePaths;
            @Nullable
            public final Map<FieldKey, String> fieldKeyValueMap;
            @Nullable
            private ArtworkInfo artworkInfo;

            private LoadingInfo(Collection<String> filePaths, @Nullable Map<FieldKey, String> fieldKeyValueMap, @Nullable ArtworkInfo artworkInfo) {
                this.filePaths = filePaths;
                this.fieldKeyValueMap = fieldKeyValueMap;
                this.artworkInfo = artworkInfo;
            }
        }
    }

    public static class ArtworkInfo {
        public final int albumId;
        public final Bitmap artwork;

        public ArtworkInfo(int albumId, Bitmap artwork) {
            this.albumId = albumId;
            this.artwork = artwork;
        }
    }

    private void setUpImageView() {
        loadCurrentImage();
        final CharSequence[] items = new CharSequence[]{
                getString(R.string.download_from_last_fm),
                getString(R.string.pick_from_local_storage),
                getString(R.string.web_search),
                getString(R.string.remove_cover)
        };
        image.setOnClickListener(v -> new MaterialDialog.Builder(AbsTagEditorActivity.this)
                .title(R.string.update_image)
                .items(items)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            getImageFromLastFM();
                            break;
                        case 1:
                            startImagePicker();
                            break;
                        case 2:
                            searchImageOnWeb();
                            break;
                        case 3:
                            deleteImage();
                            break;
                    }
                }).show());
    }

    private void getIntentExtras() {
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            id = intentExtras.getLong(EXTRA_ID);
        }
    }

    private void startImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_from_local_storage)), REQUEST_CODE_SELECT_IMAGE);
    }

    protected void dataChanged() {
        showFab();
    }

    private void showFab() {
        fab.animate()
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .scaleX(1)
                .scaleY(1)
                .start();
        fab.setEnabled(true);
    }

    protected abstract void loadCurrentImage();

    protected abstract void getImageFromLastFM();

    protected abstract void searchImageOnWeb();

    protected abstract void deleteImage();

    protected abstract int getContentViewLayout();

    @NonNull
    protected abstract List<String> getSongPaths();

    protected abstract void save();

    protected long getId() {
        return id;
    }

    @NonNull
    private AudioFile getAudioFile(@NonNull String path) {
        try {
            return AudioFileIO.read(new File(path));
        } catch (Exception e) {
            Log.e(TAG, "Could not read audio file " + path, e);
            return new AudioFile();
        }
    }

    @Nullable
    protected String getSongTitle() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.TITLE);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected String getAlbumTitle() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.ALBUM);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected String getArtistName() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.ARTIST);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected String getAlbumArtistName() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.ALBUM_ARTIST);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected String getGenreName() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.GENRE);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected String getSongYear() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.YEAR);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected String getTrackNumber() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.TRACK);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected String getLyrics() {
        try {
            return getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirst(FieldKey.LYRICS);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    protected Bitmap getAlbumArt() {
        try {
            Artwork artworkTag = getAudioFile(songPaths.get(0)).getTagOrCreateAndSetDefault().getFirstArtwork();
            if (artworkTag != null) {
                byte[] artworkBinaryData = artworkTag.getBinaryData();
                return BitmapFactory.decodeByteArray(artworkBinaryData, 0, artworkBinaryData.length);
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
