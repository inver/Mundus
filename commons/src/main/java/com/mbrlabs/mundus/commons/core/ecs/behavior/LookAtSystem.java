package com.mbrlabs.mundus.commons.core.ecs.behavior;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.mbrlabs.mundus.commons.core.ecs.component.CameraComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@All(CameraComponent.class)
public class LookAtSystem extends IteratingSystem {

    protected ComponentMapper<CameraComponent> cameraComponentMapper;

    @Override
    protected void process(int entityId) {
        //todo
//        log.info(cameraComponentMapper.get(entityId).);
    }
}
