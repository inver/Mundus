package com.mbrlabs.mundus.commons.assets.shader;

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ShaderAsset extends Asset<ShaderMeta> {

    private String vertexShader;
    private String fragmentShader;

    public ShaderAsset(Meta<ShaderMeta> meta) {
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

//    @Override
//    public boolean usesAsset(Asset assetToCheck) {
//        return false;
//    }
}
