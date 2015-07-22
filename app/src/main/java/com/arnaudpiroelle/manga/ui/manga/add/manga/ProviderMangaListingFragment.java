package com.arnaudpiroelle.manga.ui.manga.add.manga;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.adapter.BaseAdapter;
import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.event.MangaSelectedEvent;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.ui.manga.add.manga.ProviderMangaListingPresenter.ProviderMangaListingPresenterCallback;
import com.arnaudpiroelle.manga.ui.manga.list.MangaView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;
import static com.arnaudpiroelle.manga.model.History.HistoryBuilder.createHisotry;

public class ProviderMangaListingFragment extends Fragment
        implements ProviderMangaListingPresenterCallback, OnRefreshListener, OnQueryTextListener {

    public static final String PROVIDER_NAME = "providerName";

    @Inject ProviderRegistry providerRegistry;
    @Inject EventBus eventBus;

    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list_provider_mangas) ListView listView;

    private ProviderMangaListingPresenter presenter;

    private MangaProvider provider = null;
    private BaseAdapter<Manga, MangaView> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GRAPH.inject(this);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            String providerName = bundle.getString(PROVIDER_NAME);
            provider = providerRegistry.get(providerName);
        }

        adapter = new BaseAdapter<>(getActivity(), R.layout.item_view_provider_manga, new ArrayList<Manga>());

        presenter = new ProviderMangaListingPresenter(this, provider);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_provider_mangas, container, false);

        ButterKnife.bind(this, view);

        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.title_provider_mangas);

        presenter.list();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_provider_mangas, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getActivity());
        item.setActionView(searchView);

        searchView.setQueryHint(getString(R.string.search_query_hint));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onListingLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onListingLoaded(List<Manga> mangas) {
        adapter.setData(mangas);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onListingFiltered(List<Manga> mangas) {
        adapter.setData(mangas);
    }

    @Override
    public void onRefresh() {
        presenter.list();
    }

    @OnItemClick(R.id.list_provider_mangas)
    public void onMangaClick(int position){
        Manga manga = adapter.getItem(position);

        eventBus.post(new MangaSelectedEvent(manga));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        presenter.filter(query);
        return true;
    }
}
