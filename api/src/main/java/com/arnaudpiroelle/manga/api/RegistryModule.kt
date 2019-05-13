package com.arnaudpiroelle.manga.api

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

val registryModule = module(createdAtStart = true) {
    single { ProviderRegistry() }
}

fun plugin(declaration: ModuleDeclaration) = module(createdAtStart = true, moduleDeclaration = declaration)