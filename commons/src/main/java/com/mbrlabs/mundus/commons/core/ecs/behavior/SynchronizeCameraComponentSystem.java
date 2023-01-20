package com.mbrlabs.mundus.commons.core.ecs.behavior;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.mbrlabs.mundus.commons.core.ecs.component.CameraComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;

@All({PositionComponent.class, CameraComponent.class})
public class SynchronizeCameraComponentSystem extends IteratingSystem {

    protected ComponentMapper<CameraComponent> cameraMapper;
    protected ComponentMapper<PositionComponent> positionMapper;

    @Override
    protected void process(int entityId) {
        var camera = cameraMapper.get(entityId).getCamera();
        var positionComponent = positionMapper.get(entityId);
        camera.combined.set(positionComponent.getTransform());
    }
}
