package com.mbrlabs.mundus.editor.ui.components.camera;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class LookAtBehavior extends BaseCameraHandleBehavior {

    private final Vector3 tmp1 = new Vector3();
    private final Vector3 tmp2 = new Vector3();

    @Override
    public void doAction(GameObject object) {
        getCamera(object).getPosition(tmp1);
        getHandle(object).getPosition(tmp2);
        tmp2.sub(tmp1).nor();

        double yaw = Math.toDegrees(Math.atan(tmp2.x / tmp2.z));
        if (tmp2.z > 0) {
            yaw += 180;
        }
        double pitch = 90 - Math.toDegrees(Math.acos(tmp2.y));

        var quat = new Quaternion().setEulerAngles((float) yaw, (float) pitch, 0).nor();
        getCamera(object).setLocalRotation(quat.x, quat.y, quat.z, quat.w);
    }
}
