package com.mbrlabs.mundus.commons.assets.material;

import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MaterialAssetLoader implements AssetLoader<MaterialAsset, MaterialMeta> {

    @Override
    public MaterialAsset load(Meta<MaterialMeta> meta) {
        var asset = new MaterialAsset(meta);
        asset.load();
        return asset;
    }


}
