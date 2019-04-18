/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.rak.dj.djmusicplayer.musiclibrary.artists;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.glide.ArtistGlideRequest;
import com.rak.dj.djmusicplayer.glide.JazzColoredTarget;
import com.rak.dj.djmusicplayer.helpers.ColorUtil;
import com.rak.dj.djmusicplayer.helpers.JazzUtil;
import com.rak.dj.djmusicplayer.helpers.MaterialValueHelper;
import com.rak.dj.djmusicplayer.helpers.NavigationUtil;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.models.upgraded.Artist;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<Artist> arraylist;
    private Activity mContext;
    private boolean isGrid;
    protected boolean usePalette = false;

    public ArtistAdapter(Activity context, ArrayList<Artist> arraylist, boolean usePalette) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.usePalette = usePalette;
        this.isGrid = PreferencesUtils.getInstance(mContext).isArtistsInGrid();
    }

    public static int getOpaqueColor(@ColorInt int paramInt) {
        return 0xFF000000 | paramInt;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isGrid) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_grid, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        final Artist localItem = arraylist.get(i);

        itemHolder.name.setText(localItem.getName());
        String albumNumber = JazzUtil.makeLabel(mContext, R.plurals.Nalbums, localItem.getAlbumCount());
        String songCount = JazzUtil.makeLabel(mContext, R.plurals.Nsongs, localItem.getSongCount());
        itemHolder.albums.setText(JazzUtil.makeCombinedString(mContext, albumNumber, songCount));

        loadArtistImage(localItem, itemHolder);

        if (JazzUtil.isLollipop())
            itemHolder.artistImage.setTransitionName("transition_artist_art" + i);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if (arraylist == null || arraylist.size() == 0)
            return "";
        return Character.toString(arraylist.get(position).getName().charAt(0));
    }

    private void setColors(int color, ItemHolder holder) {
        if (holder.footer != null) {
            holder.footer.setBackgroundColor(color);
            if (holder.name != null) {
                holder.name.setTextColor(MaterialValueHelper.getPrimaryTextColor(mContext, ColorUtil.isColorLight(color)));
            }
            if (holder.name != null) {
                holder.name.setTextColor(MaterialValueHelper.getSecondaryTextColor(mContext, ColorUtil.isColorLight(color)));
            }
        }
    }

    protected void loadArtistImage(Artist artist, final ItemHolder holder) {
        if (holder.artistImage == null) return;
        ArtistGlideRequest.Builder.from(Glide.with(mContext), artist)
                .generatePalette(mContext).build()
                .into(new JazzColoredTarget(holder.artistImage) {
                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                        setColors(getDefaultFooterColor(), holder);
                    }

                    @Override
                    public void onColorReady(int color) {
                        if (usePalette)
                            setColors(color, holder);
                        else
                            setColors(getDefaultFooterColor(), holder);
                    }
                });
    }

    public void usePalette(boolean usePalette) {
        this.usePalette = usePalette;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return arraylist.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public void updateDataSet(ArrayList<Artist> data) {
        this.arraylist = data;
        notifyDataSetChanged();
    }

    public ArrayList<Artist> getDataSet() {
        return arraylist;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView name, albums;
        protected ImageView artistImage;
        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.artist_name);
            this.albums = (TextView) view.findViewById(R.id.album_song_count);
            this.artistImage = (ImageView) view.findViewById(R.id.artistImage);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtil.navigateToArtist(mContext, arraylist.get(getAdapterPosition()).getId(),
                    new Pair<View, String>(artistImage, "transition_artist_art" + getAdapterPosition()));
        }

    }
}




