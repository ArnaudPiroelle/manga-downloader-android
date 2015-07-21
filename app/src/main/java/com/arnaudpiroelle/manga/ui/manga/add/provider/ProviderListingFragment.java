package com.arnaudpiroelle.manga.ui.manga.add.provider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.adapter.BaseAdapter;
import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.event.ProviderSelectedEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;

public class ProviderListingFragment extends Fragment implements ProviderListingPresenter.ProviderListingCallback, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    ProviderRegistry providerRegistry;

    @Inject
    EventBus eventBus;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.list_provider)
    ListView listView;

    private Presenter<MangaProvider> presenter;
    private BaseAdapter<MangaProvider, MangaProviderView> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GRAPH.inject(this);

        adapter = new BaseAdapter<>(getActivity(),
                R.layout.item_view_provider,
                new ArrayList<>()
        );

        presenter = new ProviderListingPresenter(this, providerRegistry);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_provider, container, false);

        ButterKnife.bind(this, view);

        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.title_providers);

        presenter.list();
    }

    @Override
    public void onListingLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onListingLoaded(List<MangaProvider> providers) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setData(providers);
    }

    @Override
    public void onRefresh() {
        presenter.list();
    }

    @OnItemClick(R.id.list_provider)
    public void onClickProvider(int position) {
        MangaProvider provider = adapter.getItem(position);
        eventBus.post(new ProviderSelectedEvent(provider.getName()));
    }

}
