package com.arnaudpiroelle.manga.ui.manga.add.chapter;

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
import com.arnaudpiroelle.manga.event.ChapterSelectedEvent;
import com.arnaudpiroelle.manga.event.MangaSelectedEvent;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;

public class ProviderMangaChaptersListingFragment extends Fragment implements ProviderMangaChaptersListingPresenter.ProviderMangaListingPresenterCallback, SwipeRefreshLayout.OnRefreshListener {

    public static final String MANGA = "MANGA";

    @Inject ProviderRegistry providerRegistry;
    @Inject EventBus eventBus;


    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list_provider_manga_chapters) ListView listView;

    private ProviderMangaChaptersListingPresenter presenter;

    private BaseAdapter<Chapter, ChapterView> adapter;
    private Manga manga;
    private MangaProvider provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GRAPH.inject(this);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            manga = bundle.getParcelable(MANGA);
            provider = providerRegistry.get(manga.getProvider());
        }

        adapter = new BaseAdapter<>(getActivity(), R.layout.item_view_provider_manga_chapter, new ArrayList<>());

        presenter = new ProviderMangaChaptersListingPresenter(this, provider, manga);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_provider_manga_chapters, container, false);

        ButterKnife.bind(this, view);

        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.title_provider_manga_chapters);

        presenter.list();
    }

    @Override
    public void onListingLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onListingLoaded(List<Chapter> chapters) {
        adapter.setData(chapters);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        presenter.list();
    }

    @OnItemClick(R.id.list_provider_manga_chapters)
    public void onChapterClick(int position){
        Chapter chapter = adapter.getItem(position);

        manga.setLastChapter(chapter.getChapterNumber());

        eventBus.post(new ChapterSelectedEvent(manga));
    }
}
