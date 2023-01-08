package com.mbrlabs.mundus.editor.ui.components.camera;

import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.behavior.Behavior;

public abstract class BaseCameraHandleBehavior implements Behavior {

    protected GameObject getCamera(GameObject go) {
        if (go.getChildren() == null) {
            return null;
        }
        return go.getChildren().get(1);
    }

    protected GameObject getHandle(GameObject go) {
        if (go.getChildren() == null) {
            return null;
        }
        return go.getChildren().get(0);
    }
}
