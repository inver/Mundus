package com.mbrlabs.mundus.commons.core.ecs.behavior;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;

@All({PositionComponent.class, RenderComponent.class})
public class SynchronizeRenderComponentSystem extends IteratingSystem {

    protected ComponentMapper<RenderComponent> renderMapper;
    protected ComponentMapper<PositionComponent> positionMapper;

    @Override
    protected void process(int entityId) {
        renderMapper.get(entityId).getRenderable().setPosition(positionMapper.get(entityId).getTransform());
    }
}
