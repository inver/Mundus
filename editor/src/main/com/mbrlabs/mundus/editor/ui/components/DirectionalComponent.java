package com.mbrlabs.mundus.editor.ui.components;

import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;

public abstract class DirectionalComponent extends AbstractComponent {
    protected Vector3 pointOfView;
    protected Vector3 position;

    public DirectionalComponent(GameObject go) {
        super(go);
    }

}
