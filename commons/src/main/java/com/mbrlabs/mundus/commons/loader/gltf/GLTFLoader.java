package com.mbrlabs.mundus.commons.loader.gltf;

import com.badlogic.gdx.files.FileHandle;
import net.mgsx.gltf.loaders.gltf.SeparatedDataFileResolver;

public class GLTFLoader extends GLTFLoaderBase {
    public SceneAsset load(FileHandle glFile) {
        return load(glFile, false);
    }

    public SceneAsset load(FileHandle glFile, boolean withData) {
        SeparatedDataFileResolver dataFileResolver = new SeparatedDataFileResolver();
        dataFileResolver.load(glFile);
        return load(dataFileResolver, withData);
    }

}