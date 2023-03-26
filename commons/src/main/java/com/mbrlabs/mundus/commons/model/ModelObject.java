package com.mbrlabs.mundus.commons.model;

import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObject;
import lombok.Getter;
import net.nevinsky.mundus.core.ModelInstance;
import net.nevinsky.mundus.core.Renderable;
import net.nevinsky.mundus.core.model.Model;

import java.util.List;

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
    public void getRenderables(List<Renderable> renderables, Pool<Renderable> pool) {
        modelInstance.getRenderables(renderables, pool);
    }
}
