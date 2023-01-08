package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import lombok.Getter;

@Getter
public class CameraInstanceComponent extends AbstractComponent {

    protected final Camera camera;

    public CameraInstanceComponent(GameObject go) {
        this(go, new PerspectiveCamera());
    }

    public CameraInstanceComponent(GameObject go, Camera camera) {
        super(go);
        this.camera = camera;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {

    }
}
