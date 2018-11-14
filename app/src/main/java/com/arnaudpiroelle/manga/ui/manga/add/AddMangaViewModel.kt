package com.arnaudpiroelle.manga.ui.manga.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.core.utils.combineLatest
import com.arnaudpiroelle.manga.core.utils.map
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

    private val filter = MutableLiveData<String>().apply { value = "" }
    private val providers = MutableLiveData<List<ProviderSpinnerAdapter.Provider>>()
    private val mangas = MutableLiveData<List<Manga>>()
    val wizardStatus = MutableLiveData<WizardStatus>()

    enum class WizardStatus {
        STARTED,
        FINISHED
    }

    fun loadProviders() {
        providers.value = providerRegistry.list()
                .map { ProviderSpinnerAdapter.Provider(it.key, it.value) }
    }

    fun selectProvider(provider: ProviderSpinnerAdapter.Provider) {

        launch {
            val items = withContext(IO) {
                provider.mangaProvider.findMangas()
            }

            mangas.value = items
        }
    }

    fun getProviders(): LiveData<List<ProviderSpinnerAdapter.Provider>> {
        return providers
    }

    fun getMangas(): LiveData<List<Manga>> {
        return filter.combineLatest(mangas)
                .map { result ->
                    result.second.filter { it.name.toLowerCase().contains(result.first) }
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
            wizardStatus.value = WizardStatus.FINISHED
        }
    }

    fun filter(name: String) {
        filter.value = name
    }
}
