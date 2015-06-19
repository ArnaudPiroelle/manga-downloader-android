package com.arnaudpiroelle.manga.ui.manga.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingPresenter.MangaListingCallback;
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment;
import com.arnaudpiroelle.manga.ui.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;

public class MangaListingActivity extends AppCompatActivity implements MangaListingCallback, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    EventBus eventBus;

    @Inject
    ProviderRegistry providerRegistry;

    @InjectView(R.id.list_manga)
    ListView listView;

    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    BaseAdapter<Manga, MangaView> adapter;
    Presenter<Manga> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_manga);

        setTitle(R.string.title_mymangas);

        GRAPH.inject(this);
        ButterKnife.inject(this);

        adapter = new BaseAdapter<>(this, R.layout.item_view_manga, new ArrayList<>());

        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        presenter = new MangaListingPresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                manualDownload();
                return true;
            case R.id.action_settings:
                goSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        eventBus.register(this);
        presenter.list();
    }

    @Override
    protected void onPause() {
        eventBus.unregister(this);

        super.onPause();
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
        startActivity(new Intent(this, AddMangaActivity.class));
    }

    public void manualDownload() {
        startService(new Intent(this, DownloadService.class));
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

    public void onEventMainThread(MangaUpdatedEvent event){
        event.manga.save();
        presenter.list();

        manualDownload();
    }

    private void modifyManga(Manga manga) {
        Observable.<List<Chapter>>create((subscriber) -> {
            subscriber.onNext(providerRegistry.get(manga.getProvider()).findChaptersFor(manga));
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((chapters) -> {

                    manga.setChapters(chapters);

                    ModifyMangaDialogFragment modifyMangaDialogFragment = new ModifyMangaDialogFragment();
                    modifyMangaDialogFragment.setManga(manga);
                    modifyMangaDialogFragment.show(getFragmentManager(), null);


                });
    }

    private void removeManga(Manga manga) {
        manga.delete();
        presenter.list();
    }
}
