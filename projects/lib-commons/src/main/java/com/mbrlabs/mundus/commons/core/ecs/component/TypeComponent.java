package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TypeComponent extends Component {

    private Type type;

    public TypeComponent(Type type) {
        this.type = type;
    }

    public enum Type {
        GROUP,
        LIGHT_DIRECTIONAL,
        LIGHT_POINT,
        LIGHT_SPOT,
        OBJECT,
        CAMERA,
        HANDLE
    }
}
