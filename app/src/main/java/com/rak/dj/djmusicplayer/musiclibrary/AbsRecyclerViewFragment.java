package com.rak.dj.djmusicplayer.musiclibrary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rak.dj.djmusicplayer.JazzMusicPlayerApp;
import com.rak.dj.djmusicplayer.R;
import com.rak.dj.djmusicplayer.helpers.PreferencesUtils;
import com.rak.dj.djmusicplayer.helpers.ThemeStore;
import com.rak.dj.djmusicplayer.helpers.ViewUtil;
import com.rak.dj.djmusicplayer.widgets.DividerItemDecoration;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AbsRecyclerViewFragment<A extends RecyclerView.Adapter> extends Fragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.list_empty)
    TextView empty;

    protected PreferencesUtils mPreferences;
    protected boolean isGrid;
    protected GridLayoutManager layoutManager;
    protected RecyclerView.ItemDecoration itemDecoration;
    protected AppCompatActivity mContext;

    private A adapter;

    // To prevent fragment not attached exception for now, calling fragment on back button pressed issue
    public Resources getAppResources(){
        return JazzMusicPlayerApp.getInstance().getResources();
    }

    /* http://stackoverflow.com/a/2888433 */
    @Override
    public LoaderManager getLoaderManager() {
        return getParentFragment().getLoaderManager();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtils.getInstance(getActivity());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        initLayoutManager();
        initAdapter();
        setupRecyclerView(rootView);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initLayoutManager() {
        if (getGrid()) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }
    }

    private void initAdapter() {
        adapter = createAdapter();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIsEmpty();
            }
        });
    }

    protected abstract boolean getGrid();

    private void setupRecyclerView(View rootView) {
        if (recyclerView instanceof FastScrollRecyclerView) {
            ViewUtil.setUpFastScrollRecyclerViewColor(getActivity(), ((FastScrollRecyclerView) recyclerView), ThemeStore.accentColor(getActivity()));
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void updateLayoutManager(int column) {
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.setAdapter(getAdapter());
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        setItemDecoration();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @NonNull
    protected A getAdapter(){
        return adapter;
    }

    @NonNull
    protected abstract A createAdapter();

    @SuppressLint("SupportAnnotationUsage")
    @StringRes
    protected abstract String getEmptyMessage();

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.fragment_recylerview;
    }

    protected void setItemDecoration() {
            if (getGrid()) {
                recyclerView.removeItemDecoration(itemDecoration);
                int spacingInPixels = getAppResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
                itemDecoration = new SpacesItemDecoration(spacingInPixels);
            } else {
                itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
            }
            recyclerView.addItemDecoration(itemDecoration);
    }

    private void checkIsEmpty() {
        if (empty != null) {
            empty.setText(getEmptyMessage());
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    protected abstract boolean loadUsePalette();

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.top = space;
            outRect.right = space;
            outRect.bottom = space;

        }
    }


}
