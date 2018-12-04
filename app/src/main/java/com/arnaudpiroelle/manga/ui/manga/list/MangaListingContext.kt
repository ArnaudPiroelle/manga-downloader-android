package com.arnaudpiroelle.manga.ui.manga.list

import androidx.annotation.StringRes
import com.arnaudpiroelle.manga.ui.core.Action
import com.arnaudpiroelle.manga.ui.core.State

data class MangaListingState(@StringRes val notificationResId: Int? = null) : State

sealed class MangaListingAction : Action
object StartSyncAction : MangaListingAction()
object DismissNotificationAction : MangaListingAction()