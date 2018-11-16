package com.arnaudpiroelle.manga.api.model

import java.io.File

interface PostProcess {
    fun execute(file: File)
}

enum class PostProcessType {
    NONE,
    MOSAIC
}

data class Page(var url: String, var postProcess: PostProcessType = PostProcessType.NONE)