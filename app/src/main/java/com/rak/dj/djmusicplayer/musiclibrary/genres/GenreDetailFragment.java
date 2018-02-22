package com.rak.dj.djmusicplayer.musiclibrary.genres;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.upgraded.GenreLoader;
import com.rak.dj.djmusicplayer.glide.JazzColoredTarget;
import com.rak.dj.djmusicplayer.glide.SongGlideRequest;
import com.rak.dj.djmusicplayer.glide.palette.BitmapPaletteWrapper;
import com.rak.dj.djmusicplayer.helpers.ATEUtils;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.helpers.Helpers;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.models.upgraded.Genre;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musiclibrary.AbsThemedMusicLibraryFragment;
import com.rak.dj.djmusicplayer.musicplayerutils.MusicPlayer;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenreDetailFragment extends AbsThemedMusicLibraryFragment {


    private int genreId = -1;

    private ImageView genreArt, artistArt;
    private TextView genreTitle, genreDetails;
    private AppCompatActivity mContext;

    private RecyclerView recyclerView;
    private GenreSongsAdapter mAdapter;

    private Genre genre;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    private boolean loadFailed = false;

    private PreferencesUtils mPreferences;
    private Context context;
    private int primaryColor = -1;

    public GenreDetailFragment() {
        // Required empty public constructor
    }

    public static GenreDetailFragment newInstance(Genre genre, boolean useTransition, String transitionName) {
        GenreDetailFragment fragment = new GenreDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.GENRE_ID, genre);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genreId = getArguments().getInt(Constants.GENRE_ID);
        }
        context = getActivity();
        mContext = (AppCompatActivity) context;
        mPreferences = PreferencesUtils.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_genre_details, container, false);

        genreArt = rootView.findViewById(R.id.genre_art);
        artistArt = rootView.findViewById(R.id.artist_art);
        genreTitle = rootView.findViewById(R.id.genre_title);
        genreDetails = rootView.findViewById(R.id.genre_details);

        toolbar = rootView.findViewById(R.id.toolbar);

        fab = rootView.findViewById(R.id.fab);

        if (getArguments().getBoolean("transition")) {
            genreArt.setTransitionName(getArguments().getString("transition_name"));
        }
        recyclerView = rootView.findViewById(R.id.recyclerview);
        collapsingToolbarLayout = rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout = rootView.findViewById(R.id.app_bar);
        recyclerView.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        genre = getArguments().getParcelable(Constants.GENRE_ID);

        setUpEverything();

        fab.setOnClickListener( v ->{
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                GenreSongsAdapter adapter = (GenreSongsAdapter) recyclerView.getAdapter();
                MusicPlayer.playAll(getActivity(), adapter.getSongIds(), 0, genreId, JazzUtil.IdType.Album, true);
                NavigationUtil.navigateToNowplaying(getActivity(), false);
            }, 150);
        });

        return rootView;

    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(genre.name);
    }

    private void setGenreDetails() {
        String songCount = JazzUtil.makeLabel(getActivity(), R.plurals.Nsongs, genre.songCount);
        //String year = (album.getYear() != 0) ? (" - " + String.valueOf(album.getYear())) : "";
        genreTitle.setText(genre.name);
        //albumDetails.setText(album.getArtistName() + " - " + songCount + year);
    }

    private void setUpGenreSongs(){
        ArrayList<Song> genreSongs = GenreLoader.getSongs(getActivity(), genre.id);
        mAdapter = new GenreSongsAdapter((AppCompatActivity) getActivity(), genreSongs, genre.id);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
    }

    public Song safeGetFirstSong(List<Song> songs) {
        return songs.isEmpty() ? Song.EMPTY_SONG : songs.get(0);
    }

    private void setColors(int color) {
        //toolbarColor = color;
        //albumTitleView.setBackgroundColor(color);
        //albumTitleView.setTextColor(MaterialValueHelper.getPrimaryTextColor(this, ColorUtil.isColorLight(color)));

        //setNavigationbarColor(color);
        //setTaskDescriptionColor(color);
    }

    private void loadGenreCover(){
        SongGlideRequest.Builder.from(Glide.with(this), safeGetFirstSong(GenreLoader.getSongs(getActivity(), genre.id)))
                .checkIgnoreMediaStore(getActivity())
                .generatePalette(getActivity()).build()
                .dontAnimate()
                .listener(new RequestListener<Object, BitmapPaletteWrapper>() {
                    @Override
                    public boolean onException(Exception e, Object model, Target<BitmapPaletteWrapper> target, boolean isFirstResource) {
                        getActivity().supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(BitmapPaletteWrapper resource, Object model, Target<BitmapPaletteWrapper> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getActivity().supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(new JazzColoredTarget(genreArt) {
                    @Override
                    public void onColorReady(int color) {
                        setColors(color);
                    }
                });
    }

    private void setUpEverything() {
        setupToolbar();
        setGenreDetails();
        setUpGenreSongs();
        loadGenreCover();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        String ateKey = Helpers.getATEKey(getActivity());
        ATEUtils.setStatusBarColor(getActivity(), ateKey, Config.primaryColor(getActivity(), ateKey));
    }


}
