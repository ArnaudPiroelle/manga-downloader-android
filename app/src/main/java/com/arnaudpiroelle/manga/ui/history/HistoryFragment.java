package com.arnaudpiroelle.manga.ui.history;

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
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.History;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;

public class HistoryFragment extends Fragment implements HistoryPresenter.HistoryListingCallback, SwipeRefreshLayout.OnRefreshListener {

    @Inject EventBus eventBus;

    @InjectView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.list_history) ListView listView;

    Presenter<History> presenter;
    BaseAdapter<History, HistoryView> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GRAPH.inject(this);

        presenter = new HistoryPresenter(this);
        adapter = new BaseAdapter<>(getActivity(), R.layout.item_view_history);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_history, container, false);

        ButterKnife.inject(this, view);

        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.title_history);

        presenter.list();
    }

    @Override
    public void onListingLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onListingLoaded(List<History> histories) {
        swipeRefreshLayout.setRefreshing(false);

        adapter.setData(histories);
    }

    @Override
    public void onRefresh() {
        presenter.list();
    }
}
