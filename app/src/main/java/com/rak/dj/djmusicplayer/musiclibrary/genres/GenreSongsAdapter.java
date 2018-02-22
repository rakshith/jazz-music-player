package com.rak.dj.djmusicplayer.musiclibrary.genres;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.models.upgraded.Song;
import com.rak.dj.djmusicplayer.musiclibrary.AbsSongsAdapter;
import com.rak.dj.djmusicplayer.musiclibrary.AlbumSongsAdapter;
import com.rak.dj.djmusicplayer.musiclibrary.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sraksh on 2/19/2018.
 */

public class GenreSongsAdapter extends AbsSongsAdapter<Song, GenreSongsAdapter.ItemHolder> {

    private ArrayList<Song> arraylist;
    private AppCompatActivity mContext;
    private long albumID;
    private long[] songIDs;
    private int itemPosition;

    public GenreSongsAdapter(AppCompatActivity context, ArrayList<Song> arraylist, long genreId) {
        super(context, arraylist);
        this.arraylist = arraylist;
        this.mContext = context;
        this.songIDs = getSongIds();
        this.albumID = genreId;
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    @Override
    public void updateDataSet(ArrayList<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
    }

    @Override
    public void removeSongAt(int i){
        arraylist.remove(i);
    }

    @Override
    public GenreSongsAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_song, null);
        GenreSongsAdapter.ItemHolder ml = new GenreSongsAdapter.ItemHolder(v);
        return ml;
    }

    @Override
    public void genericBindViewHolder(GenreSongsAdapter.ItemHolder itemHolder, int position) {
        this.itemPosition = position;
        Song localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.duration.setText(JazzUtil.makeShortTimeString(mContext, (localItem.duration) / 1000));
        int tracknumber = localItem.trackNumber;
        if (tracknumber == 0) {
            itemHolder.trackNumber.setText("-");
        } else itemHolder.trackNumber.setText(String.valueOf(tracknumber));

        setOnPopupMenuListener(itemHolder, position);
    }

    private void setOnPopupMenuListener(GenreSongsAdapter.ItemHolder itemHolder, final int position) {

        itemHolder.menu.setOnClickListener(v -> {

            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.setOnMenuItemClickListener(menuItem ->  {

                menuFunctionalityForSong(mContext, menuItem, position, -1, songIDs);

                return false;
            });
            menu.inflate(R.menu.popup_song);
            menu.show();
        });
    }

    public class ItemHolder extends BaseViewHolder implements View.OnClickListener {
        protected TextView title, duration, trackNumber;
        protected ImageView menu;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.duration = (TextView) view.findViewById(R.id.song_duration);
            this.trackNumber = (TextView) view.findViewById(R.id.trackNumber);
            this.menu = (ImageView) view.findViewById(R.id.popup_menu);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(() -> playAll(mContext, songIDs, getAdapterPosition(), albumID,
                    JazzUtil.IdType.Album, false,
                    arraylist.get(getAdapterPosition()), true), 100);
        }

    }
}
