package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import lombok.Getter;

public class LightComponent extends Component {
    @Getter
    private  final transient DirectionalLight light;

    public LightComponent() {
        this.light = new DirectionalLight();
    }
}
