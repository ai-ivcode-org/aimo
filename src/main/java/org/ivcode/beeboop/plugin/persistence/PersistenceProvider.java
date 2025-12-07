package org.ivcode.beeboop.plugin.persistence;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public interface PersistenceProvider {

    @NotNull
    Datastore<InputStream> createFileStore(@NotNull String collection);

    @NotNull
    <V> Datastore<V> createPersistentStore(@NotNull String collection, @NotNull Class<V> valueType);

    @NotNull
    <V> Datastore<V> createCacheStore(@NotNull String collection, @NotNull Class<V> valueType);
}
