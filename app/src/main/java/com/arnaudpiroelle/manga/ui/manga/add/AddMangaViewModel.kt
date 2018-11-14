package com.arnaudpiroelle.manga.ui.manga.add

import androidx.lifecycle.ViewModel
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.core.utils.createDefaultLiveData
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.data.model.Task
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class AddMangaViewModel(
        private val providerRegistry: ProviderRegistry,
        private val taskDao: TaskDao,
        private val mangaDao: MangaDao) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    val state = createDefaultLiveData(State())

    fun loadProviders() {
        val providers = providerRegistry.list().map { ProviderSpinnerAdapter.Provider(it.key, it.value) }
        state.value = state.value?.copy(isLoading = true, providers = providers)
    }

    fun selectProvider(provider: ProviderSpinnerAdapter.Provider) {
        launch {
            val items = withContext(IO) {
                provider.mangaProvider.findMangas()
            }

            state.value = state.value?.copy(isLoading = false, results = items)
        }
    }

    override fun onCleared() {
        job.cancel()
    }

    fun selectManga(manga: Manga) {
        launch {
            withContext(IO) {
                val mangaId = mangaDao.insert(com.arnaudpiroelle.manga.data.model.Manga(name = manga.name, alias = manga.alias, provider = manga.provider))
                taskDao.insert(Task(type = Task.Type.RETRIEVE_CHAPTERS, label = manga.name, item = mangaId))
            }

            state.value = state.value?.copy(status = WizardStatus.FINISHED)
        }
    }

    fun filter(query: String) {
        state.value = state.value?.copy(filter = query)
    }

    enum class WizardStatus {
        STARTED,
        FINISHED
    }

    data class State(
            val isLoading: Boolean = false,
            val status: WizardStatus = WizardStatus.STARTED,
            val providers: List<ProviderSpinnerAdapter.Provider> = listOf(),
            val results: List<Manga> = listOf(),
            val filter: String = ""
    ) {
        fun getFilteredResults() = results.filter { it.name.toLowerCase().contains(filter) }
    }
}
