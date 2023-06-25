package com.mbrlabs.mundus.commons.model;

import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.loader.AssimpWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ModelService {

    private final AssimpWorker assimpWorker;

    //todo may be change to ModelObject?
    public ModelObject createFromAsset(ModelAsset asset) {
        var modelFileName = asset.getMeta().getAdditional().getFile();
        var model = assimpWorker.loadModel(asset.getMeta().getFile().child(modelFileName));
        return new ModelObject(asset.getName(), model);
    }

}