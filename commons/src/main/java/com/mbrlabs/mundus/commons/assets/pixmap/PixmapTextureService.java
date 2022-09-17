package com.mbrlabs.mundus.commons.assets.pixmap;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetService;
import com.mbrlabs.mundus.commons.assets.meta.dto.Meta;
import org.apache.commons.lang3.NotImplementedException;

public class PixmapTextureService implements AssetService<PixmapTextureAsset> {
    @Override
    public void save(PixmapTextureAsset asset) {
        throw new NotImplementedException();
    }

    @Override
    public PixmapTextureAsset load(Meta meta, FileHandle assetFile) {
        throw new NotImplementedException();
    }

    @Override
    public void copy(FileHandle from, FileHandle to) {

    }
}
