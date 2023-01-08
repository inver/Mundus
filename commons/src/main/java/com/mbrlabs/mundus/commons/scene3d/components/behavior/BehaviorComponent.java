package com.mbrlabs.mundus.commons.scene3d.components.behavior;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;

public class BehaviorComponent extends AbstractComponent {

    private final Behavior behavior;

    public BehaviorComponent(GameObject go, Behavior behavior) {
        super(go);
        type = Type.ACTION;
        this.behavior = behavior;
    }

    @Override
    public void update(float delta) {
        behavior.doAction(gameObject);
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        //do nothing
    }
}
