package com.mbrlabs.mundus.editor.utils;

import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.shaders.ModelShader;
import com.mbrlabs.mundus.editor.scene3d.components.PickableModelComponent;

public final class GameObjectUtils {
    public static GameObject createModelGO(SceneGraph sg, ModelShader shader, int id, String name, ModelAsset asset) {
        var res = new GameObject(sg, name, id);

//        asset.
        var modelComponent = new PickableModelComponent(res, shader);
        modelComponent.setModel(asset, true);
        res.getComponents().add(modelComponent);
        modelComponent.encodeRaypickColorId();

        return res;
    }
}
