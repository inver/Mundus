package com.mbrlabs.mundus.commons.assets.skybox;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.mundus.core.ModelBuilder;
import net.nevinsky.mundus.core.ModelInstance;

@Slf4j
@RequiredArgsConstructor
public class SkyboxAssetLoader implements AssetLoader<SkyboxAsset, SkyboxMeta> {
    @Override
    public SkyboxAsset load(Meta<SkyboxMeta> meta) {
        var cubemap = new Cubemap(
                loadTexture(meta, meta.getAdditional().getBack()).getTextureData(),
                loadTexture(meta, meta.getAdditional().getFront()).getTextureData(),
                loadTexture(meta, meta.getAdditional().getLeft()).getTextureData(),
                loadTexture(meta, meta.getAdditional().getRight()).getTextureData(),
                loadTexture(meta, meta.getAdditional().getBottom()).getTextureData(),
                loadTexture(meta, meta.getAdditional().getTop()).getTextureData()
        );

        var model = new ModelBuilder().createBox(1, 1, 1,
                new Material(new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap)),
                VertexAttributes.Usage.Position);

        var asset = new SkyboxAsset(meta);
        asset.setBoxInstance(new ModelInstance(model));
        log.debug(meta.toString());
        return asset;
    }

    private Texture loadTexture(Meta<SkyboxMeta> meta, String fileName) {
        var filePath = meta.getFile().child(fileName);
        log.debug("Load texture for file: " + filePath);
        if (!filePath.exists()) {
            throw new IllegalStateException("File not found: " + filePath.path());
        }
        return new Texture(filePath);
    }
}
