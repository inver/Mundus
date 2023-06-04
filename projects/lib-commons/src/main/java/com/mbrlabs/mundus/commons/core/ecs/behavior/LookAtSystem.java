package com.mbrlabs.mundus.commons.core.ecs.behavior;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@All(PositionComponent.class)
public class LookAtSystem extends IteratingSystem {

    //todo check concurrency
    private final Vector3 tmp1 = new Vector3();
    private final Vector3 tmp2 = new Vector3();
    private final Quaternion quat = new Quaternion();

    protected ComponentMapper<PositionComponent> mapper;

    @Override
    protected void process(int entityId) {
        var component = mapper.get(entityId);
        int lookAt = component.getLookAtId();
        if (lookAt < 0) {
            return;
        }
        tmp1.set(component.getLocalPosition());
        tmp2.set(world.getEntity(lookAt).getComponent(PositionComponent.class).getLocalPosition());

        tmp2.sub(tmp1).nor();

        double yaw = Math.toDegrees(Math.atan(tmp2.x / tmp2.z));
        if (tmp2.z > 0) {
            yaw += 180;
        }
        if (Double.isNaN(yaw)) {
            yaw = 90;
        }
        double pitch = 90 - Math.toDegrees(Math.acos(tmp2.y));

        quat.setEulerAngles((float) yaw, (float) pitch, 0).nor();
        component.getLocalRotation().set(quat.x, quat.y, quat.z, quat.w);
    }
}
