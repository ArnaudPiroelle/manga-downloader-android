package com.arnaudpiroelle.manga.service.task

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.arnaudpiroelle.manga.data.ChapterRepository
import com.arnaudpiroelle.manga.data.MangaRepository
import com.arnaudpiroelle.manga.data.PageRepository
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.Page
import com.arnaudpiroelle.manga.data.model.Task
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrieveChaptersTaskExecution @Inject constructor(
        private val mangaRepository: MangaRepository,
        private val chapterRepository: ChapterRepository,
        private val pageRepository: PageRepository
) : TaskExecution {

    override fun execute(task: Task): Completable {
        val mangaId = task.id

        return mangaRepository.getById(mangaId)
                .flatMapCompletable(this::fetchChapters)
    }

    private fun fetchChapters(manga: Manga): Completable {
        val provider = ProviderRegistry.find(manga.provider)

        return provider.findChapters(manga.alias)
                .flatMapObservable { Observable.fromIterable(it) }
                .map { Chapter(name = it.name, number = it.chapterNumber, mangaId = manga.id, status = Chapter.Status.WANTED) }
                .flatMapCompletable { addChapter(provider, manga, it) }
    }

    private fun addChapter(provider: MangaProvider, manga: Manga, chapter: Chapter): Completable {
        return chapterRepository.insert(chapter)
                .flatMapCompletable { chapterId ->
                    fetchPages(provider, manga, chapter.copy(id = chapterId))
                }
    }

    private fun fetchPages(provider: MangaProvider, manga: Manga, chapter: Chapter): Completable {
        return provider.findPages(manga.alias, chapter.number)
                .flatMapObservable { Observable.fromIterable(it) }
                .map {
                    val postProcess = when (it.postProcess) {
                        PostProcessType.NONE -> Page.PostProcess.NONE
                        PostProcessType.MOSAIC -> Page.PostProcess.MOSAIC
                    }
                    Page(chapterId = chapter.id, url = it.url, postProcess = postProcess)
                }
                .flatMapCompletable { pageRepository.insert(it).ignoreElement() }

    }

}