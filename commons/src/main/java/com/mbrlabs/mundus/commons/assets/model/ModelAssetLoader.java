package com.mbrlabs.mundus.commons.assets.model;

import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.dto.Meta;

public class ModelAssetLoader implements AssetLoader<ModelAsset, ModelMeta> {

    @Override
    public ModelAsset load(Meta<ModelMeta> meta) {
        var asset = new ModelAsset(meta);
        asset.load();
        return asset;
    }

}
