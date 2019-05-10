package com.arnaudpiroelle.manga.ui.history

import androidx.annotation.StringRes
import com.arnaudpiroelle.manga.ui.common.BaseAction
import com.arnaudpiroelle.manga.ui.common.BaseState

interface HistoryContext {
    sealed class Action : BaseAction {
        object CleanHistoryAction : Action()
    }

    data class State(val visualNotification: VisualNotification? = null) : BaseState

    data class VisualNotification(@StringRes val resId: Int)
}