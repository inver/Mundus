package com.mbrlabs.mundus.commons.assets.terrain;

import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.dto.Meta;

public class TerrainAssetLoader implements AssetLoader<TerrainAsset, TerrainMeta> {
    @Override
    public TerrainAsset load(Meta<TerrainMeta> meta) {
        TerrainAsset asset = new TerrainAsset(meta);
        asset.load();
        return asset;
    }

}
