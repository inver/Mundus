package com.mbrlabs.mundus.commons.assets.terrain;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetService;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import org.apache.commons.lang3.NotImplementedException;

public class TerrainService implements AssetService<TerrainAsset> {
    @Override
    public void save(TerrainAsset asset) {
        throw new NotImplementedException();
    }

    @Override
    public TerrainAsset load(Meta meta, FileHandle assetFile) {
        throw new NotImplementedException();
    }
}
