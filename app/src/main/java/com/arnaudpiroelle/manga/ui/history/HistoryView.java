package com.arnaudpiroelle.manga.ui.history;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView;
import com.arnaudpiroelle.manga.model.History;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HistoryView extends LinearLayout implements BaseItemView<History>{

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy hh:mm", Locale.getDefault());

    @InjectView(R.id.history_date) TextView date;
    @InjectView(R.id.history_label) TextView label;

    public HistoryView(Context context) {
        super(context);
    }

    public HistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    public void bindView(History history) {
        label.setText(history.getLabel());
        date.setText(simpleDateFormat.format(new Date(history.getDate())));
    }
}
