package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import lombok.Getter;

public class CameraComponent extends Component {
    @Getter
    private final PerspectiveCamera camera;

    public CameraComponent() {
        camera = new PerspectiveCamera();
    }
}
