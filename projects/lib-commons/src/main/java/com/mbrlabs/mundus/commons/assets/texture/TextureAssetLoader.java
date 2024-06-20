package com.mbrlabs.mundus.commons.assets.texture;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Slf4j
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

        if (filePath.type() != Files.FileType.Classpath) {
            asset.setSize(FileUtils.sizeOf(filePath.file()) / 1_000_000f);
        } else {
            try {

                var resource = getClass().getClassLoader().getResource(filePath.path());
                if (resource != null) {
                    var uri = resource.toURI();
                    var bytes = new File(uri).length();
                    asset.setSize(bytes / 1_000f);
                }
            } catch (Exception e) {
                log.error("ERROR", e);
            }
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
