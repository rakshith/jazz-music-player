package com.rak.dj.djmusicplayer.musiclibrary.video;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.VideoLoader;
import com.rak.dj.djmusicplayer.helpers.misc.WrappedAsyncTaskLoader;
import com.rak.dj.djmusicplayer.models.AlbumVideo;
import com.rak.dj.djmusicplayer.musiclibrary.AbsRecyclerViewFragment;
import com.rak.dj.djmusicplayer.searchmanager.LoaderIds;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends AbsRecyclerViewFragment<AlbumVideoAdapter> implements LoaderManager.LoaderCallbacks<ArrayList<AlbumVideo>>{

    public static final String TAG = VideoFragment.class.getSimpleName();

    private static final int LOADER_ID = LoaderIds.VIDEO_FRAGMENT;

    @Override
    protected boolean getGrid() {
        return false;
    }

    @NonNull
    @Override
    protected AlbumVideoAdapter createAdapter() {
        ArrayList<AlbumVideo> dataSet = getAdapter() == null ? new ArrayList<AlbumVideo>() : getAdapter().getDataSet();
        return new AlbumVideoAdapter((AppCompatActivity) getActivity(), dataSet, loadUsePalette());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected String getEmptyMessage() {
        return getAppResources().getString(R.string.no_video_albums);
    }

    @Override
    protected boolean loadUsePalette() {
        return false;
    }

    @Override
    public Loader<ArrayList<AlbumVideo>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncVideoAlbumLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AlbumVideo>> loader, ArrayList<AlbumVideo> data) {
        getAdapter().updateDataSet(data);
        setItemDecoration();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AlbumVideo>> loader) {
        getAdapter().updateDataSet(new ArrayList<AlbumVideo>());
        //setItemDecoration();
    }

    private static class AsyncVideoAlbumLoader extends WrappedAsyncTaskLoader<ArrayList<AlbumVideo>> {
        public AsyncVideoAlbumLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<AlbumVideo> loadInBackground() {
            return VideoLoader.getAlbumVideos(getContext());
        }
    }
}
