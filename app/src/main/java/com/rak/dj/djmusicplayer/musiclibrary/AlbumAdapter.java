package com.rak.dj.djmusicplayer.musiclibrary;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.glide.JazzColoredTarget;
import com.rak.dj.djmusicplayer.glide.SongGlideRequest;
import com.rak.dj.djmusicplayer.helpers.ColorUtil;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.MaterialValueHelper;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.models.upgraded.Album;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

/**
 * Created by sraksh on 1/24/2018.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemHolder> implements FastScrollRecyclerView.SectionedAdapter{
    private List<Album> arraylist;
    private AppCompatActivity mContext;
    private boolean isGrid;

    public AlbumAdapter(AppCompatActivity context, List<Album> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtils.getInstance(mContext).isAlbumsInGrid();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isGrid) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_grid, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_list, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    public boolean loadUsePalette() {
        return PreferencesUtils.getInstance(mContext).albumColoredFooters();
    }

    protected void setColors(int color, ItemHolder holder) {
        if (holder.footer != null) {
            holder.footer.setBackgroundColor(color);
            if (holder.title != null) {
                holder.title.setTextColor(MaterialValueHelper.getPrimaryTextColor(mContext, ColorUtil.isColorLight(color)));
            }
            if (holder.title != null) {
                holder.title.setTextColor(MaterialValueHelper.getSecondaryTextColor(mContext, ColorUtil.isColorLight(color)));
            }
        }
    }

    protected void loadAlbumCover(Album album, final ItemHolder holder) {
        if (holder.albumArt == null) return;
        SongGlideRequest.Builder.from(Glide.with(mContext), album.safeGetFirstSong())
                .checkIgnoreMediaStore(mContext)
                .generatePalette(mContext).build()
                .into(new JazzColoredTarget(holder.albumArt) {
                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                        if (isGrid) {
                            setColors(getDefaultFooterColor(), holder);
                        }
                    }

                    @Override
                    public void onColorReady(int color) {
                        if (isGrid) {
                            if (loadUsePalette())
                                setColors(color, holder);
                            else
                                setColors(getDefaultFooterColor(), holder);
                        }
                    }
                });
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int position) {
        Album localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.getTitle());
        itemHolder.artist.setText(localItem.getArtistName());

        loadAlbumCover(localItem, itemHolder);

        /*ImageLoader.getInstance().displayImage(JazzUtil.getAlbumArtUri(localItem.id).toString(), itemHolder.albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(400))
                        .build(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (isGrid) {
                            new Palette.Builder(loadedImage).generate(palette ->  {
                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                if (swatch != null) {
                                    int color = swatch.getRgb();
                                    itemHolder.footer.setBackgroundColor(color);
                                    int textColor = JazzUtil.getBlackWhiteColor(swatch.getTitleTextColor());
                                    itemHolder.title.setTextColor(textColor);
                                    itemHolder.artist.setTextColor(textColor);
                                } else {
                                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                    if (mutedSwatch != null) {
                                        int color = mutedSwatch.getRgb();
                                        itemHolder.footer.setBackgroundColor(color);
                                        int textColor = JazzUtil.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                                        itemHolder.title.setTextColor(textColor);
                                        itemHolder.artist.setTextColor(textColor);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (isGrid) {
                            itemHolder.footer.setBackgroundColor(0);
                            if (mContext != null) {
                                int textColorPrimary = Config.textColorPrimary(mContext, Helpers.getATEKey(mContext));
                                itemHolder.title.setTextColor(textColorPrimary);
                                itemHolder.artist.setTextColor(textColorPrimary);
                            }
                        }
                    }
                });*/

        if (JazzUtil.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + position);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if (arraylist == null || arraylist.size() == 0)
            return "";
        Character ch = arraylist.get(position).getTitle().charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, artist;
        protected ImageView albumArt;
        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.album_title);
            this.artist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtil.navigateToAlbum(mContext, arraylist.get(getAdapterPosition()).getId(),
                    new Pair<View, String>(albumArt, "transition_album_art" + getAdapterPosition()));
        }
    }


    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public void updateDataSet(List<Album> arraylist) {
        this.arraylist = arraylist;
    }

}
