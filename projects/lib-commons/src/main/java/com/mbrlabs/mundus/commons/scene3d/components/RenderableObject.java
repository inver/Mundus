package com.mbrlabs.mundus.commons.scene3d.components;

import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import net.nevinsky.abyssus.core.ModelBatch;

import static net.nevinsky.abyssus.core.shader.ShaderProvider.DEFAULT_SHADER_KEY;

public interface RenderableObject {
    /**
     * Calls the render() method for each component in this and all child nodes.
     *
     * @param delta time since last render
     */
    default void render(ModelBatch batch, SceneEnvironment environment, float delta) {
        render(batch, environment, getShaderKey(), delta);
    }

    default String getShaderKey() {
        return DEFAULT_SHADER_KEY;
    }

    void render(ModelBatch batch, SceneEnvironment environment, String shaderKey, float delta);
}
