package com.mbrlabs.mundus.commons.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableSceneObject;
import lombok.Getter;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.model.Model;

public class ModelObject implements RenderableSceneObject {

    @Getter
    private final String assetName;
    private final transient Model model;
    @Getter
    private transient ModelInstance modelInstance;

    public ModelObject(String assetName, Model model) {
        this.assetName = assetName;
        this.model = model;
        modelInstance = new ModelInstance(model);
    }

    private void recalculateModel() {

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
