package com.mbrlabs.mundus.commons.core.ecs.behavior;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.Point2PointPositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import lombok.extern.slf4j.Slf4j;

@All(Point2PointPositionComponent.class)
public class SynchronizeRenderPoint2PointSystem extends IteratingSystem {

    protected ComponentMapper<PositionComponent> mapper;
    protected ComponentMapper<Point2PointPositionComponent> p2pMapper;
    protected ComponentMapper<RenderComponent> renderMapper;

    @Override
    protected void process(int entityId) {
        var comp = p2pMapper.get(entityId);

        var e1 = mapper.get(comp.getEntity1Id());
        var e2 = mapper.get(comp.getEntity2Id());
        if (e1 == null || e2 == null) {
            return;
        }
        renderMapper.get(entityId).getRenderable().set2PointPosition(e1.getLocalPosition(), e2.getLocalPosition());
    }
}
