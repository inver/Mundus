package com.mbrlabs.mundus.commons.assets.texture;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetService;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import org.apache.commons.lang3.NotImplementedException;

public class TextureService implements AssetService<TextureAsset> {
    @Override
    public void save(TextureAsset asset) {
        throw new NotImplementedException();
    }

    @Override
    public TextureAsset load(Meta meta, FileHandle assetFile) {
        TextureAsset asset = new TextureAsset(meta, assetFile);
        // TODO parse special texture instead of always setting them
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();
        return asset;
    }
}
