package com.arnaudpiroelle.manga.ui.manga.add

import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Manga.Status.ADDED
import com.arnaudpiroelle.manga.ui.core.BaseViewModel
import com.arnaudpiroelle.manga.ui.manga.add.ProviderSpinnerAdapter.Provider
import com.arnaudpiroelle.manga.worker.TaskManager
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class AddMangaViewModel(
        private val providerRegistry: ProviderRegistry,
        private val mangaDao: MangaDao,
        private val taskManager: TaskManager) : BaseViewModel<AddMangaAction, AddMangaState>(AddMangaState()), CoroutineScope {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    private fun loadProviders() {
        launch {
            val providers = providerRegistry.list().map { Provider(it.key, it.value) }

            updateState { state -> state.copy(providers = providers) }
        }
    }

    private fun selectProvider(provider: Provider) {
        launch {
            updateState { state -> state.copy(isLoading = true, error = null) }

            try {
                val items = withContext(IO) {
                    provider.mangaProvider.findMangas()
                }
                updateState { state -> state.copy(isLoading = false, results = items) }
            } catch (e: Exception) {
                updateState { state -> state.copy(isLoading = false, error = ActionError(R.string.error_occured, SelectProviderAction(provider))) }
            }
        }
    }

    private fun selectManga(manga: Manga) {
        launch {
            withContext(IO) {
                val mangaId = mangaDao.insert(com.arnaudpiroelle.manga.data.model.Manga(name = manga.name, alias = manga.alias, provider = manga.provider, thumbnail = "", status = ADDED))

                taskManager.scheduleAddManga(mangaId)
            }
            updateState { state -> state.copy(status = WizardStatus.FINISHED) }
        }
    }

    private fun filter(query: String) {
        updateState { state -> state.copy(filter = query) }
    }

    override fun onCleared() {
        job.cancel()
    }

    override fun handle(action: AddMangaAction) {
        when (action) {
            is LoadProvidersAction -> loadProviders()
            is SelectProviderAction -> selectProvider(action.provider)
            is FilterResultsAction -> filter(action.query)
            is AddNewMangaAction -> selectManga(action.manga)
        }
    }
}
