package com.arnaudpiroelle.manga.core.provider;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static com.arnaudpiroelle.manga.core.provider.ProviderRegistry.ProviderRegistryBuilder.createProviderRegister;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProviderRegistryTest {

    @Before
    public void setup() {
    }

    @Test
    public void should_retrieve_provider_from_registry() throws Exception {
        // Given / When
        MangaProvider provider = Mockito.mock(MangaProvider.class);
        when(provider.getName()).thenReturn("myProvider");

        ProviderRegistry registry = Companion.createProviderRegister()
                .withProvider(provider)
                .build();

        // Then
        assertThat(registry.list()).containsOnly(provider);
        assertThat(registry.get("myProvider")).isEqualTo(provider);
    }
}