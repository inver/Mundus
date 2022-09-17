package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.meta.dto.Meta;

public interface AssetService<T extends Asset> {
    void save(T asset);

    T load(Meta meta, FileHandle assetFile);

    void copy(FileHandle from, FileHandle to);
}
