package com.arnaudpiroelle.manga.ui.manga.add

import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.interactors.AddMangaInteractor
import com.arnaudpiroelle.manga.interactors.GetProviderMangasInteractor
import com.arnaudpiroelle.manga.interactors.GetProvidersInteractor
import com.arnaudpiroelle.manga.ui.common.BaseViewModel
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaContext.*
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaContext.Action.*
import com.arnaudpiroelle.manga.worker.TaskManager

class AddMangaViewModel(
        private val taskManager: TaskManager,
        private val getProvidersInteractor: GetProvidersInteractor,
        private val getProviderMangasInteractor: GetProviderMangasInteractor,
        private val addMangaInteractor: AddMangaInteractor) : BaseViewModel<Action, State>(State()) {

    override suspend fun onHandle(action: Action) {
        when (action) {
            is LoadProvidersAction -> loadProviders()
            is SelectProviderAction -> selectProvider(action.provider)
            is FilterResultsAction -> filter(action.query)
            is AddNewMangaAction -> selectManga(action.manga)
        }
    }

    private suspend fun loadProviders() {
        val providers = getProvidersInteractor()
        updateState { state -> state.copy(providers = providers) }
    }

    private suspend fun selectProvider(provider: ProviderRegistry.Provider) {
        updateState { state -> state.copy(isLoading = true, error = null) }

        try {
            val items = getProviderMangasInteractor(provider)
            updateState { state -> state.copy(isLoading = false, results = items) }
        } catch (e: Exception) {
            updateState { state -> state.copy(isLoading = false, error = ActionError(R.string.error_occured, SelectProviderAction(provider))) }
        }
    }

    private suspend fun selectManga(manga: Manga) {
        val mangaId = addMangaInteractor(manga)

        taskManager.scheduleAddManga(mangaId)
        updateState { state -> state.copy(status = WizardStatus.FINISHED) }
    }

    private fun filter(query: String) {
        updateState { state -> state.copy(filter = query) }
    }
}
