package com.mbrlabs.mundus.commons.assets.model;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetService;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import org.apache.commons.lang3.NotImplementedException;

public class ModelService implements AssetService<ModelAsset> {
    @Override
    public void save(ModelAsset asset) {
        throw new NotImplementedException();
    }

    @Override
    public ModelAsset load(Meta meta, FileHandle assetFile) {
        throw new NotImplementedException();
    }
}
