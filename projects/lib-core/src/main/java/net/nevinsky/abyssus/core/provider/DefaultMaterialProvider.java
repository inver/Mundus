package net.nevinsky.abyssus.core.provider;

import com.badlogic.gdx.graphics.g3d.Material;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultMaterialProvider extends AbstractAssetInstanceProvider<Material> {

    @Override
    protected Material create(String name) {
        return null;
    }
}
