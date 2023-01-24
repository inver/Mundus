package com.mbrlabs.mundus.commons.assets.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;

public class TextureAssetLoader implements AssetLoader<TextureAsset, TextureMeta> {

    @Override
    public TextureAsset load(Meta<TextureMeta> meta) {
        var asset = new TextureAsset(meta);
        var filePath = meta.getFile().child(meta.getAdditional().getFile());
        if (asset.isGenerateMipMaps()) {
            asset.setTexture(loadMipmapTexture(filePath));
        } else {
            asset.setTexture(new Texture(filePath));
        }

        if (asset.isTileable()) {
            asset.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }

        return asset;
    }

    private Texture loadMipmapTexture(FileHandle fileHandle) {
        Texture texture = new Texture(fileHandle, true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        return texture;
    }
}
