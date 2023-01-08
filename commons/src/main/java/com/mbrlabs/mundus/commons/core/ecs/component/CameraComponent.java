package com.mbrlabs.mundus.commons.core.ecs.component;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.core.ecs.base.BaseComponent;
import lombok.Getter;

public class CameraComponent extends BaseComponent {
    @Getter
    private transient final Camera camera;

    public CameraComponent() {
        camera = new PerspectiveCamera();
    }
}
