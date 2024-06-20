package net.nevinsky.abyssus.core.provider;

public interface AssetInstanceProvider<T> {
    T get(String name);
}
