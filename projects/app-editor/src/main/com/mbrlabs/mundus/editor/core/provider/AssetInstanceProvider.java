package com.mbrlabs.mundus.editor.core.provider;

public interface AssetInstanceProvider<T> {
    T load(String name);
}
