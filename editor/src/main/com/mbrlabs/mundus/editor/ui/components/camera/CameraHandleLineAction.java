package com.mbrlabs.mundus.editor.ui.components.camera;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.ui.components.EditorHandleComponent;

public class CameraHandleLineAction extends BaseCameraHandleAction {
    private final Vector3 tmp1 = new Vector3();
    private final Vector3 tmp2 = new Vector3();
    private final Quaternion rotation = new Quaternion();

    @Override
    public void doAction(GameObject object) {
        var camera = getCamera(object.getParent());
        camera.getPosition(tmp1);
        camera.getRotation(rotation);
        var handle = getHandle(object.getParent());
        handle.getPosition(tmp2);
        tmp2.sub(tmp1);

        EditorHandleComponent component = (EditorHandleComponent) handle.getComponents().get(0);

        component.getHandleLineInstance().transform.set(tmp1, rotation);
        component.getHandleLineInstance().transform.scale(tmp2.len(), tmp2.len(), tmp2.len());
    }
}
