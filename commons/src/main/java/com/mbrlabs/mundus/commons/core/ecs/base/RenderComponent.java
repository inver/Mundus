package com.mbrlabs.mundus.commons.core.ecs.base;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RenderComponent extends Component {
    @Getter
    private RenderableDelegate renderable;

    RenderComponent(RenderableDelegate renderable) {
        this.renderable = renderable;
    }
}
