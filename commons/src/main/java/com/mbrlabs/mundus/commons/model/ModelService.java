package com.mbrlabs.mundus.commons.model;

import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ModelService {

//    private final GLTFLoader gltfLoader = new GLTFLoader();

    //todo may be change to ModelObject?
    public ModelObject createFromAsset(ModelAsset asset) {
        var modelFileName = asset.getMeta().getAdditional().getFile();
//        var sceneAsset = gltfLoader.load(asset.getMeta().getFile().child(modelFileName), true);
//        var res = new ModelObject(asset.getName(), sceneAsset.scene.getModel());
//        return res;
        return null;
    }

}
