package com.rak.dj.djmusicplayer.playlistmanager;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.LastAddedLoader;
import com.rak.dj.djmusicplayer.dataloaders.PlaylistLoader;
import com.rak.dj.djmusicplayer.dataloaders.PlaylistSongLoader;
import com.rak.dj.djmusicplayer.dataloaders.SongLoader;
import com.rak.dj.djmusicplayer.dataloaders.TopTracksLoader;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.helpers.JazzUtils;
import com.rak.dj.djmusicplayer.helpers.NavigationUtils;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtility;
import com.rak.dj.djmusicplayer.models.Playlist;
import com.rak.dj.djmusicplayer.models.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistPagerFragment extends Fragment implements DragLayout.GotoDetailListener{

    private ImageView imageView;
    private View address1, address2, address3, address4, address5;
    private RatingBar ratingBar;
    private View head1, head2, head3, head4;
    private String imageUrl;

    private static final String ARG_PAGE_NUMBER = "pageNumber";
    private int[] foregroundColors = {R.color.pink_transparent, R.color.green_transparent, R.color.blue_transparent, R.color.red_transparent, R.color.purple_transparent};
    private int pageNumber, songCountInt, totalRuntime;
    private int foregroundColor;
    private long firstAlbumID = -1;
    private Playlist playlist;
    private TextView playlistame, songcount, playlistnumber, playlisttype, runtime;
    private ImageView playlistImage;
    private View foreground;
    private Context mContext;
    private boolean showAuto;

    public PlaylistPagerFragment() {
        // Required empty public constructor
    }

    public static PlaylistPagerFragment newInstance(int pageNumber) {
        PlaylistPagerFragment fragment = new PlaylistPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        showAuto = PreferencesUtility.getInstance(getActivity()).showAutoPlaylist();

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);

        pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        playlist = playlists.get(pageNumber);

        View rootView = inflater.inflate(R.layout.fragment_playlistpager, null);
        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        playlistImage = (ImageView) dragLayout.findViewById(R.id.playlist_image);
        //ImageLoader.getInstance().displayImage(imageUrl, imageView);
        playlistame = dragLayout.findViewById(R.id.name);
        playlisttype = dragLayout.findViewById(R.id.playlisttype);
        songcount = (TextView) rootView.findViewById(R.id.songcount);
        address2 = dragLayout.findViewById(R.id.address2);
        playlistnumber = dragLayout.findViewById(R.id.playlistnumber);
        runtime = dragLayout.findViewById(R.id.runtime);
        ratingBar = (RatingBar) dragLayout.findViewById(R.id.rating);
        foreground = rootView.findViewById(R.id.foreground);

        dragLayout.setGotoDetailListener(this);

        mContext = this.getContext();
        setUpPlaylistDetails();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedinstancestate) {
        new loadPlaylistImage().execute("");
    }

    private void setUpPlaylistDetails() {
        playlistame.setText(playlist.name);

        int number = getArguments().getInt(ARG_PAGE_NUMBER) + 1;
        String playlistnumberstring;

        if (number > 9) {
            playlistnumberstring = String.valueOf(number);
        } else {
            playlistnumberstring = "0" + String.valueOf(number);
        }
        playlistnumber.setText(playlistnumberstring);

        Random random = new Random();
        int rndInt = random.nextInt(foregroundColors.length);

        foregroundColor = foregroundColors[rndInt];
        foreground.setBackgroundColor(foregroundColor);

        if (showAuto) {
            if (pageNumber <= 2)
                playlisttype.setVisibility(View.VISIBLE);
        }

    }

    private String getPlaylistType() {
        if (showAuto) {
            switch (pageNumber) {
                case 0:
                    return Constants.NAVIGATE_PLAYLIST_LASTADDED;
                case 1:
                    return Constants.NAVIGATE_PLAYLIST_RECENT;
                case 2:
                    return Constants.NAVIGATE_PLAYLIST_TOPTRACKS;
                default:
                    return Constants.NAVIGATE_PLAYLIST_USERCREATED;
            }
        } else return Constants.NAVIGATE_PLAYLIST_USERCREATED;
    }

    private class loadPlaylistImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null) {
                if (showAuto) {
                    switch (pageNumber) {
                        case 0:
                            List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(getActivity());
                            songCountInt = lastAddedSongs.size();
                            for(Song song : lastAddedSongs) {
                                totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                            }
                            if (songCountInt != 0) {
                                firstAlbumID = lastAddedSongs.get(0).albumId;
                                return JazzUtils.getAlbumArtUri(firstAlbumID).toString();
                            } else return "nosongs";
                        case 1:
                            TopTracksLoader recentloader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.RecentSongs);
                            List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                            songCountInt = recentsongs.size();
                            for(Song song : recentsongs){
                                totalRuntime += song.duration / 1000;
                            }

                            if (songCountInt != 0) {
                                firstAlbumID = recentsongs.get(0).albumId;
                                return JazzUtils.getAlbumArtUri(firstAlbumID).toString();
                            } else return "nosongs";
                        case 2:
                            TopTracksLoader topTracksLoader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.TopTracks);
                            List<Song> topsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                            songCountInt = topsongs.size();
                            for(Song song : topsongs){
                                totalRuntime += song.duration / 1000;
                            }
                            if (songCountInt != 0) {
                                firstAlbumID = topsongs.get(0).albumId;
                                return JazzUtils.getAlbumArtUri(firstAlbumID).toString();
                            } else return "nosongs";
                        default:
                            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getActivity(), playlist.id);
                            songCountInt = playlistsongs.size();
                            for(Song song : playlistsongs){
                                totalRuntime += song.duration;
                            }
                            if (songCountInt != 0) {
                                firstAlbumID = playlistsongs.get(0).albumId;
                                return JazzUtils.getAlbumArtUri(firstAlbumID).toString();
                            } else return "nosongs";

                    }
                } else {
                    List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getActivity(), playlist.id);
                    songCountInt = playlistsongs.size();
                    for(Song song : playlistsongs){
                        totalRuntime += song.duration;
                    }
                    if (songCountInt != 0) {
                        firstAlbumID = playlistsongs.get(0).albumId;
                        return JazzUtils.getAlbumArtUri(firstAlbumID).toString();
                    } else return "nosongs";
                }
            } else return "context is null";

        }

        @Override
        protected void onPostExecute(String uri) {
            ImageLoader.getInstance().displayImage(uri, playlistImage,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .build(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }
                    });
            songcount.setText(" " + String.valueOf(songCountInt) + " " + mContext.getString(R.string.songs));
            runtime.setText(" " + JazzUtils.makeShortTimeString(mContext, totalRuntime));
        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void gotoDetail() {
        /*Activity activity = (Activity) getContext();
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(imageView, PlaylistDetailActivity.IMAGE_TRANSITION_NAME),
                new Pair(address1, PlaylistDetailActivity.ADDRESS1_TRANSITION_NAME),
                new Pair(address2, PlaylistDetailActivity.ADDRESS2_TRANSITION_NAME),
                new Pair(address3, PlaylistDetailActivity.ADDRESS3_TRANSITION_NAME),
                new Pair(address4, PlaylistDetailActivity.ADDRESS4_TRANSITION_NAME),
                new Pair(address5, PlaylistDetailActivity.ADDRESS5_TRANSITION_NAME),
                new Pair(ratingBar, PlaylistDetailActivity.RATINGBAR_TRANSITION_NAME),
                new Pair(head1, PlaylistDetailActivity.HEAD1_TRANSITION_NAME),
                new Pair(head2, PlaylistDetailActivity.HEAD2_TRANSITION_NAME),
                new Pair(head3, PlaylistDetailActivity.HEAD3_TRANSITION_NAME),
                new Pair(head4, PlaylistDetailActivity.HEAD4_TRANSITION_NAME)
        );
        Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(PlaylistDetailActivity.EXTRA_IMAGE_URL, imageUrl);
        ActivityCompat.startActivity(activity, intent, options.toBundle());*/

        ArrayList<Pair> tranitionViews = new ArrayList<>();
        tranitionViews.add(0, Pair.create((View) playlistame, "transition_playlist_name"));
        tranitionViews.add(1, Pair.create((View) playlistImage, "transition_album_art"));
        tranitionViews.add(2, Pair.create(foreground, "transition_foreground"));
        NavigationUtils.navigateToPlaylistDetail(getActivity(), getPlaylistType(), firstAlbumID, String.valueOf(playlistame.getText()), foregroundColor, playlist.id, tranitionViews);
    }

    public void bindData(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
