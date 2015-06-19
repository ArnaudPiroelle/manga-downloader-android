package com.arnaudpiroelle.manga.ui.manga.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView;
import com.arnaudpiroelle.manga.event.MangaActionEvent;
import com.arnaudpiroelle.manga.event.SwipeStartEvent;
import com.arnaudpiroelle.manga.model.Manga;
import com.daimajia.swipe.SwipeLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;
import static com.arnaudpiroelle.manga.event.MangaActionEvent.ActionType.MODIFY;
import static com.arnaudpiroelle.manga.event.MangaActionEvent.ActionType.REMOVE;

public class MangaView extends FrameLayout implements BaseItemView<Manga> {

    private Manga manga;

    @Inject EventBus eventBus;

    @InjectView(R.id.title) TextView title;

    @Optional
    @InjectView(R.id.chapter) TextView chapter;

    @Optional
    @InjectView(R.id.instance_swipe_layout) SwipeLayout swipeView;

    @Optional
    @InjectView(R.id.manga_action) ViewGroup mangaActionView;

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
        if(eventBus != null){
            eventBus.register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(eventBus != null){
            eventBus.unregister(this);
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    public void bindView(Manga manga) {
            GRAPH.inject(this);

        if(swipeView != null){
            setupSwipe();
        }
        this.manga = manga;

        title.setText(manga.getName());

        String lastChapter = manga.getLastChapter();
        if(lastChapter != null && !lastChapter.isEmpty()){
            chapter.setText(lastChapter);
            chapter.setVisibility(VISIBLE);
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

            }

            @Override
            public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {

            }

            @Override
            public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {

            }
        });
    }

    @Optional
    @OnClick(R.id.manga_modify)
    public void modify(){
        eventBus.post(new MangaActionEvent(manga, MODIFY));
        swipeView.close();
    }

    @Optional
    @OnClick(R.id.manga_remove)
    public void remove(){
        eventBus.post(new MangaActionEvent(manga, REMOVE));
        swipeView.close();
    }

    public void onEventMainThread(SwipeStartEvent event) {
        if (!event.manga.getName().equals(manga.getName())) {
            swipeView.close();
        }
    }
}
