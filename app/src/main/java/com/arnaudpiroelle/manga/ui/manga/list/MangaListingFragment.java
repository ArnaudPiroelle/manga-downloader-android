package com.arnaudpiroelle.manga.ui.manga.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.adapter.BaseAdapter;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.event.ChapterDownloadedEvent;
import com.arnaudpiroelle.manga.event.MangaActionEvent;
import com.arnaudpiroelle.manga.event.MangaUpdatedEvent;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.service.DownloadService;
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaActivity;
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;


public class MangaListingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MangaListingPresenter.MangaListingCallback {
    @Inject
    EventBus eventBus;
    @Inject
    ProviderRegistry providerRegistry;

    @InjectView(R.id.list_manga)
    ListView listView;
    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.manga_empty)
    View emptyView;

    BaseAdapter<Manga, MangaView> adapter;
    Presenter<Manga> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GRAPH.inject(this);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_manga, container, false);

        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BaseAdapter<>(getActivity(), R.layout.item_view_manga, new ArrayList<Manga>());

        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        swipeRefreshLayout.setOnRefreshListener(this);

        presenter = new MangaListingPresenter(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        eventBus.register(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.title_mymangas);

        presenter.list();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                manualDownload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListingLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onListingLoaded(List<Manga> mangas) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setData(mangas);
    }

    @Override
    public void onRefresh() {
        presenter.list();
    }

    @OnClick(R.id.action_add_manga)
    public void addManga() {
        getActivity().startActivity(new Intent(getActivity(), AddMangaActivity.class));
    }

    public void manualDownload() {
        getActivity().startService(new Intent(getActivity(), DownloadService.class));
    }

    public void onEventMainThread(ChapterDownloadedEvent event) {
        presenter.list();
    }

    public void onEventMainThread(MangaActionEvent event) {
        switch (event.type) {
            case MODIFY:
                modifyManga(event.manga);
                break;
            case REMOVE:
                removeManga(event.manga);
                break;
        }
    }

    public void onEventMainThread(MangaUpdatedEvent event) {
        event.manga.save();
        presenter.list();

        manualDownload();
    }

    private void modifyManga(final Manga manga) {
        Observable.create(new Observable.OnSubscribe<List<Chapter>>() {
            @Override
            public void call(Subscriber<? super List<Chapter>> subscriber) {
                subscriber.onNext(providerRegistry.get(manga.getProvider()).findChapters(manga));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Chapter>>() {
                    @Override
                    public void call(List<Chapter> chapters) {
                        manga.setChapters(chapters);

                        ModifyMangaDialogFragment modifyMangaDialogFragment = new ModifyMangaDialogFragment();
                        modifyMangaDialogFragment.setManga(manga);
                        modifyMangaDialogFragment.show(getActivity().getFragmentManager(), null);
                    }
                });
    }

    private void removeManga(Manga manga) {
        manga.delete();
        presenter.list();
    }
}
