package com.rak.dj.djmusicplayer.musiclibrary;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.musiclibrary.albums.AlbumFragment;
import com.rak.dj.djmusicplayer.musiclibrary.artists.ArtistFragment;
import com.rak.dj.djmusicplayer.musiclibrary.songs.SongsFragment;
import com.rak.dj.djmusicplayer.models.MusicLibrary;

import java.util.List;

/**
 * Created by sraksh on 1/23/2018.
 */

public class MusicLibraryAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<MusicLibraryAdapter.ItemHolder> {

    private List<MusicLibrary> dataList;

    private AppCompatActivity mContext;

    public MusicLibraryAdapter(AppCompatActivity context, List<MusicLibrary> musicLiibraryList){
        this.dataList = musicLiibraryList;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.musiclibrary_list_layout, null);
        ItemHolder ml = new MusicLibraryAdapter.ItemHolder(v);
        return ml;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView cardBackground;
        protected TextView mlTitleTv, mlCountTv;

        public ItemHolder(View itemView) {
            super(itemView);
            this.cardBackground = (ImageView)itemView.findViewById(R.id.cardBackground);
            this.mlTitleTv = (TextView)itemView.findViewById(R.id.mlTitleTv);
            this.mlCountTv = (TextView)itemView.findViewById(R.id.mlCountTv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            String musicType = mlTitleTv.getText().toString();
            Runnable navigationType;
            if(MusicLibrary.MusicType.ALBUM.toString().equals(musicType)){
                navigationType = navigateAlbum;
            }else if(MusicLibrary.MusicType.ARTIST.toString().equals(musicType)){
                navigationType = navigateArtist;
            }else{
                navigationType = navigateSongList;
            }

            final Handler handler = new Handler();
            handler.postDelayed(() -> navigationType.run(), 100);
        }
    }


    private Runnable navigateSongList = new Runnable() {
        public void run() {
            Fragment fragment = new SongsFragment();
            FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.songContainer, fragment).commit();
        }
    };

    private Runnable navigateAlbum = new Runnable() {
        public void run() {
            Fragment fragment = new AlbumFragment();
            FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.songContainer, fragment).commit();
        }
    };

    private Runnable navigateArtist = new Runnable() {
        public void run() {
            Fragment fragment = new ArtistFragment();
            FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.songContainer, fragment).commit();
        }
    };


    @Override
    public void onBindViewHolder(MusicLibraryAdapter.ItemHolder holder, int position) {
            MusicLibrary musicLibrary = dataList.get(position);
            String imageSrc = null;
            switch(musicLibrary.getMusicType()){
                case SONG:
                        imageSrc = "drawable://"+R.drawable.song_wallpaper;
                        holder.mlTitleTv.setText(musicLibrary.getMusicType().toString());
                        holder.mlCountTv.setText(""+musicLibrary.getSongList().size());
                    break;
                case ALBUM:
                        imageSrc = "drawable://"+R.drawable.album_jazz_wallpaper;
                        holder.mlTitleTv.setText(musicLibrary.getMusicType().toString());
                        holder.mlCountTv.setText(""+musicLibrary.getAlbumList().size());
                    break;
                case ARTIST:
                        imageSrc = "drawable://"+R.drawable.artisit_jazz_wallpaper;
                        holder.mlTitleTv.setText(musicLibrary.getMusicType().toString());
                        holder.mlCountTv.setText(""+musicLibrary.getArtistList().size());
                    break;
            }

        ImageLoader.getInstance().displayImage(imageSrc,
                holder.cardBackground, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }


}

