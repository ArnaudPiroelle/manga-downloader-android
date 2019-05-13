package com.arnaudpiroelle.manga.ui.manga.details

import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.ui.common.BaseAction
import com.arnaudpiroelle.manga.ui.common.BaseState

interface MangaDetailsContext {
    sealed class Action : BaseAction {
        object LoadMangaInformations : Action()
        data class RemoveMangaAction(val mangaId: Long) : Action()
        data class ChangeChapterStatusAction(val item: Chapter) : Action()
        object ChangeAllChaptersStatusAction : Action()
    }

    data class State(
            val title: String = "",
            val thumbnail: String = "",
            val origin: String = "",
            val year: String = "",
            val author: String = "",
            val type: String = "",
            val removed: Boolean = false,
            val summary: String = ""
    ) : BaseState
}