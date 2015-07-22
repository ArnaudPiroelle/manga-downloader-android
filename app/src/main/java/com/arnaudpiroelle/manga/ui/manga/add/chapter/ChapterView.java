package com.arnaudpiroelle.manga.ui.manga.add.chapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView;
import com.arnaudpiroelle.manga.model.Chapter;

public class ChapterView extends TextView implements BaseItemView<Chapter> {

    public ChapterView(Context context) {
        super(context);
    }

    public ChapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Chapter chapter) {
        setText(chapter.getChapterNumber());
    }
}
