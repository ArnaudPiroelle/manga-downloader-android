package com.arnaudpiroelle.manga.ui.manga.add

import androidx.annotation.StringRes
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.ui.core.Action
import com.arnaudpiroelle.manga.ui.core.State

sealed class AddMangaAction : Action

object LoadProvidersAction : AddMangaAction()
data class SelectProviderAction(val provider: ProviderSpinnerAdapter.Provider) : AddMangaAction()
data class FilterResultsAction(val query: String) : AddMangaAction()
data class AddNewMangaAction(val manga: Manga) : AddMangaAction()

data class ActionError(@StringRes val message: Int, val retry: AddMangaAction? = null)

data class AddMangaState(
        val isLoading: Boolean = false,
        val error: ActionError? = null,
        val status: WizardStatus = WizardStatus.STARTED,
        val providers: List<ProviderSpinnerAdapter.Provider> = listOf(),
        val results: List<Manga> = listOf(),
        val filter: String = ""
) : State {
    fun getFilteredResults() = results.filter { it.name.toLowerCase().contains(filter) }
}

enum class WizardStatus {
    STARTED,
    FINISHED
}
