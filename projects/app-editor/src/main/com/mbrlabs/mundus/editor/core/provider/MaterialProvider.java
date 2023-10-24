package com.mbrlabs.mundus.editor.core.provider;

import com.badlogic.gdx.graphics.g3d.Material;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MaterialProvider extends AbstractAssetInstanceProvider<Material> {

    private final AssetsStorage assetsStorage;

    @Override
    protected Material create(String name) {
        return null;
    }
}
