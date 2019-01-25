package com.arnaudpiroelle.manga.ui.manga.list

import androidx.annotation.StringRes
import com.arnaudpiroelle.manga.ui.core.BaseAction
import com.arnaudpiroelle.manga.ui.core.BaseState


interface MangaListingContext {

    data class State(@StringRes val notificationResId: Int? = null) : BaseState

    sealed class Action : BaseAction {
        object StartSyncAction : Action()
        object DismissNotificationAction : Action()
    }

}
