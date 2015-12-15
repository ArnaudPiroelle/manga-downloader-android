package com.arnaudpiroelle.manga.core.provider

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProviderRegistryTest {

    @Before
    fun setup() {
    }

    @Test
    @Throws(Exception::class)
    fun should_retrieve_provider_from_registry() {
        // Given / When
        val provider = Mockito.mock(MangaProvider::class.java)
        Mockito.`when`(provider.name).thenReturn("myProvider")

        val registry = ProviderRegistryBuilder.createProviderRegister().withProvider(provider).build()

        // Then
        assertThat(registry.list()).containsOnly(provider)
        assertThat(registry.find("myProvider")).isEqualTo(provider)
    }
}