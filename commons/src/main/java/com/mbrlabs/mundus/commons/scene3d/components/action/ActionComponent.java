package com.mbrlabs.mundus.commons.scene3d.components.action;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;

public class ActionComponent extends AbstractComponent {

    private final Action action;

    public ActionComponent(GameObject go, Action action) {
        super(go);
        type = Type.ACTION;
        this.action = action;
    }

    @Override
    public void update(float delta) {
        action.doAction(gameObject);
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        //do nothing
    }
}
