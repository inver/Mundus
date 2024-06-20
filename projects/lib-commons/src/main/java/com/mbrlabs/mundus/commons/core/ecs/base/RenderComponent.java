package com.mbrlabs.mundus.commons.core.ecs.base;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RenderComponent extends Component {
    private RenderableDelegate renderable;

    RenderComponent(RenderableDelegate renderable) {
        this.renderable = renderable;
    }
}
