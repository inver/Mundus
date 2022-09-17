package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.AppEnvironment;

public interface Renderable {
    /**
     * Calls the render() method for each component in this and all child nodes.
     *
     * @param delta time since last render
     */
    void render(ModelBatch batch, AppEnvironment environment, float delta);
}
