package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LightComponent extends Component {
    private final transient BaseLight light;

    public LightComponent() {
        this.light = new DirectionalLight();
    }
}
