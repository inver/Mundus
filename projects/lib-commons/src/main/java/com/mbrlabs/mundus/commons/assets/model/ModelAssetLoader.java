package com.mbrlabs.mundus.commons.assets.model;

import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModelAssetLoader implements AssetLoader<ModelAsset, ModelMeta> {

    @Override
    public ModelAsset load(Meta<ModelMeta> meta) {
        var asset = new ModelAsset(meta);
        return asset;
    }

}
