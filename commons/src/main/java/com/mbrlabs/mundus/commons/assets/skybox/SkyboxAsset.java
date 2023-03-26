package com.mbrlabs.mundus.commons.assets.skybox;

import net.nevinsky.mundus.core.ModelInstance;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
public class SkyboxAsset extends Asset<SkyboxMeta> {

    @Setter
    @Getter
    private ModelInstance boxInstance;

    public SkyboxAsset(Meta<SkyboxMeta> meta) {
        super(meta);
    }


    @Override
    public void load() {

    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {

    }

    @Override
    public void applyDependencies() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        return false;
    }
}
