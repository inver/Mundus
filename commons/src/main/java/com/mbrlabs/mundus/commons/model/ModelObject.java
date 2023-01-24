package com.mbrlabs.mundus.commons.model;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObject;
import lombok.Getter;

public class ModelObject implements RenderableObject {

    @Getter
    private final String assetName;
    private transient final Model model;
    @Getter
    private transient final ModelInstance modelInstance;

    public ModelObject(String assetName, Model model) {
        this.assetName = assetName;
        this.model = model;
        modelInstance = new ModelInstance(model);
    }

    @Override
    public AssetType getType() {
        return AssetType.MODEL;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        modelInstance.getRenderables(renderables, pool);
    }
}
