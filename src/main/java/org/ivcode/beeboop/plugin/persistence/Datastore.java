package org.ivcode.beeboop.plugin.persistence;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface Datastore<V> {
    void put(@NotNull String key, @NotNull V value);

    V get(@NotNull String key);

    @NotNull
    List<String> listKeys();

    @NotNull
    Map<String, V> list();

    void delete(@NotNull String key);
}
