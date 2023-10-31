package net.nevinsky.abyssus.core.provider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractAssetInstanceProvider<T> implements AssetInstanceProvider<T> {
    private final ConcurrentMap<String, T> cache = new ConcurrentHashMap<>();

    @Override
    public T get(String name) {
        var res = cache.computeIfAbsent(name, this::create);
        return res;
    }

    protected abstract T create(String name);
}
