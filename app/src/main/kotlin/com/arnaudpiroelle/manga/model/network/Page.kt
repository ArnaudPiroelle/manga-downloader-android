package com.arnaudpiroelle.manga.model.network

import java.io.File

interface PostProcess {
    fun execute(file: File)
}


data class Page(var url: String, var postProcess: PostProcess? = null) {
    fun getExtension(): String {
        return url.substringAfterLast(".", "jpg")
    }
}
