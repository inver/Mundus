package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;

public class SkyboxComponent extends AbstractComponent {
    public SkyboxComponent(GameObject go) {
        super(go);
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {

    }
}
