package com.mbrlabs.mundus.commons.core.ecs.base;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RenderComponent extends Component {
    @Getter
    private RenderableDelegate renderable;

    public RenderComponent(RenderableDelegate renderable) {
        this.renderable = renderable;
    }

    public static RenderComponent of(RenderableDelegate renderable) {
        return new RenderComponent(renderable);
    }
}
