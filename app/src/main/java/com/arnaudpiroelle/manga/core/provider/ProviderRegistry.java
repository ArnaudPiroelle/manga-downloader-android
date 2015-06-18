package com.arnaudpiroelle.manga.core.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderRegistry {

    Map<String, MangaProvider> registry;

    public ProviderRegistry() {
        registry = new HashMap<>();
    }

    private void register(MangaProvider mangaProvider) {
        registry.put(mangaProvider.getName(), mangaProvider);
    }

    public MangaProvider get(String providerName) {
        return registry.get(providerName);
    }

    public List<MangaProvider> list(){
        return new ArrayList<>(registry.values());
    }

    public static class ProviderRegistryBuilder {

        private ProviderRegistry providerRegistry;

        public static ProviderRegistryBuilder createProviderRegister() {
            return new ProviderRegistryBuilder();
        }

        public ProviderRegistryBuilder() {
            providerRegistry = new ProviderRegistry();
        }

        public ProviderRegistryBuilder withProvider(MangaProvider... mangaProviders) {
            for (MangaProvider mangaProvider : mangaProviders) {
                providerRegistry.register(mangaProvider);
            }

            return this;
        }

        public ProviderRegistry build() {
            return providerRegistry;
        }

    }
}
