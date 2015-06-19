package com.arnaudpiroelle.manga.ui.manga.add.provider;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView;

public class MangaProviderView extends TextView implements BaseItemView<MangaProvider> {
    public MangaProviderView(Context context) {
        super(context);
    }

    public MangaProviderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MangaProviderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void bindView(MangaProvider el) {
        setText(el.getName());
    }
}
