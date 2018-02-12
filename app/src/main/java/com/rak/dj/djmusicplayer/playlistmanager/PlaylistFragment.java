package com.rak.dj.djmusicplayer.playlistmanager;


import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.dataloaders.PlaylistLoader;
import com.rak.dj.djmusicplayer.helpers.Constants;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtility;
import com.rak.dj.djmusicplayer.models.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private List<PlaylistPagerFragment> fragments = new ArrayList<>();
    private final String[] imageArray = {"assets://image1.jpg", "assets://image2.jpg", "assets://image3.jpg", "assets://image4.jpg", "assets://image5.jpg"};

    private int playlistcount;
    private List<Playlist> playlists = new ArrayList<>();

    private PreferencesUtility mPreferences;
    private boolean isGrid;
    private boolean isDefault;
    private boolean showAuto;

    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        isGrid = mPreferences.getPlaylistView() == Constants.PLAYLIST_VIEW_GRID;
        isDefault = mPreferences.getPlaylistView() == Constants.PLAYLIST_VIEW_DEFAULT;
        showAuto = mPreferences.showAutoPlaylist();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.playlists);

        viewPager = view.findViewById(R.id.viewpager);
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();


        if (isDefault) {
            initPager();
        } else {
            //initRecyclerView();
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
    }

    private void initPager() {

        viewPager.setPageTransformer(false, new CustPagerTransformer(getActivity()));

        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return playlistcount;
            }

            @Override
            public Fragment getItem(int position) {
                return PlaylistPagerFragment.newInstance(position);
            }

        };
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    public void updatePlaylists(final long id) {
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            adapter.notifyDataSetChanged();
            if (id != -1) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    for (int i = 0; i < playlists.size(); i++) {
                        long playlistid = playlists.get(i).id;
                        if (playlistid == id) {
                            viewPager.setCurrentItem(i);
                            break;
                        }
                    }
                }, 200);
            }

        } else {
            //mAdapter.updateDataSet(playlists);
        }
    }

    public void reloadPlaylists() {
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            initPager();
        } else {
            //initRecyclerView();
        }
    }

}
