package com.mbrlabs.mundus.commons.scene3d.components;

import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import net.nevinsky.abyssus.core.ModelBatch;

public interface RenderableObject {
    /**
     * Calls the render() method for each component in this and all child nodes.
     *
     * @param delta time since last render
     */
    void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta);
}
