package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import lombok.Getter;

@Getter
public class CameraComponent extends AbstractComponent {

    protected final Camera instance;
    protected int goId = -1;

    public CameraComponent(GameObject go, Camera camera) {
        super(go);
        instance = camera;
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
