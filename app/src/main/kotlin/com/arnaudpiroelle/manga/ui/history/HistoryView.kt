package com.arnaudpiroelle.manga.ui.history

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView
import com.arnaudpiroelle.manga.model.History
import kotlinx.android.synthetic.item_view_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryView : LinearLayout, BaseItemView<History> {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun bindView(history: History) {
        history_label.text = history.label
        history_date.text = simpleDateFormat.format(Date(history.date))
    }

    companion object {

        private val simpleDateFormat = SimpleDateFormat("dd/MM/yy hh:mm", Locale.getDefault())
    }
}
