package com.mbrlabs.mundus.commons.scene3d.components;

import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

public interface RenderableObject {
    /**
     * Calls the render() method for each component in this and all child nodes.
     *
     * @param delta time since last render
     */
    void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta);
}
