package com.mbrlabs.mundus.commons.assets.skybox;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SkyboxAssetLoader implements AssetLoader<SkyboxAsset, SkyboxMeta> {
    @Override
    public SkyboxAsset load(Meta<SkyboxMeta> meta) {
        var asset = new SkyboxAsset(meta);
//        asset.setBack(loadTexture(meta, "back"));
//        asset.setFront(loadTexture(meta, "front"));
//        asset.setLeft(loadTexture(meta, "left"));
//        asset.setRight(loadTexture(meta, "right"));
//        asset.setBottom(loadTexture(meta, "bottom"));
//        asset.setTop(loadTexture(meta, "top"));

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
        asset.setBoxInstance(new ModelInstance(model));

        return asset;
    }

    private Texture loadTexture(Meta<SkyboxMeta> meta, String fileName) {
        var filePath = meta.getFile().child(fileName);
        return new Texture(filePath);
    }
}
