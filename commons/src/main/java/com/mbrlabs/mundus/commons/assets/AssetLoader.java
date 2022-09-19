package com.mbrlabs.mundus.commons.assets;

import com.mbrlabs.mundus.commons.assets.meta.Meta;

public interface AssetLoader<T extends Asset<M>, M> {
    T load(Meta<M> meta);
}
