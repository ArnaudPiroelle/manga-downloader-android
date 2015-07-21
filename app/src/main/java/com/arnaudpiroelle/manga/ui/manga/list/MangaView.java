package com.arnaudpiroelle.manga.ui.manga.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView;
import com.arnaudpiroelle.manga.event.MangaActionEvent;
import com.arnaudpiroelle.manga.event.SwipeCloseEvent;
import com.arnaudpiroelle.manga.event.SwipeStartEvent;
import com.arnaudpiroelle.manga.model.Manga;
import com.daimajia.swipe.SwipeLayout;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;
import static com.arnaudpiroelle.manga.event.MangaActionEvent.ActionType.MODIFY;
import static com.arnaudpiroelle.manga.event.MangaActionEvent.ActionType.REMOVE;

public class MangaView extends FrameLayout implements BaseItemView<Manga> {

    @Inject EventBus eventBus;

    @Bind(R.id.title) TextView title;
    @Bind(R.id.chapter) @Nullable TextView chapter;
    @Bind(R.id.instance_swipe_layout) @Nullable SwipeLayout swipeView;
    @Bind(R.id.manga_action) @Nullable ViewGroup mangaActionView;

    private Manga manga;

    public MangaView(Context context) {
        super(context);
    }

    public MangaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MangaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (eventBus != null) {
            eventBus.register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (eventBus != null) {
            eventBus.unregister(this);
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(Manga manga) {
        GRAPH.inject(this);

        if (swipeView != null) {
            setupSwipe();
        }

        this.manga = manga;

        title.setText(manga.getName());

        String lastChapter = manga.getLastChapter();
        if (lastChapter != null && !lastChapter.isEmpty()) {
            if (chapter != null) {
                chapter.setText(lastChapter);
                chapter.setVisibility(VISIBLE);
            }
        } else {
            if (chapter != null) {
                chapter.setText("");
            }
        }

    }

    private void setupSwipe() {
        swipeView.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeView.addDrag(SwipeLayout.DragEdge.Right, mangaActionView);
        swipeView.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout swipeLayout) {
                eventBus.post(new SwipeStartEvent(manga));
            }

            @Override
            public void onOpen(SwipeLayout swipeLayout) {

            }

            @Override
            public void onStartClose(SwipeLayout swipeLayout) {

            }

            @Override
            public void onClose(SwipeLayout swipeLayout) {
                eventBus.post(new SwipeCloseEvent());
            }

            @Override
            public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {

            }

            @Override
            public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {

            }
        });
    }

    @Nullable @OnClick(R.id.manga_modify) public void modify() {
        eventBus.post(new MangaActionEvent(manga, MODIFY));
        swipeView.close();
    }

    @Nullable @OnClick(R.id.manga_remove) public void remove() {
        eventBus.post(new MangaActionEvent(manga, REMOVE));
        swipeView.close();
    }

    public void onEventMainThread(SwipeStartEvent event) {
        if (!event.manga.getName().equals(manga.getName())) {
            swipeView.close();
        }
    }
}
