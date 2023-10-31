package com.mbrlabs.mundus.commons.core.ecs.base;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface RenderableDelegate extends RenderableObject {
    default void setPosition(Matrix4 position) {
        //do nothing
    }

    default void set2PointPosition(Vector3 point1, Vector3 point2) {
        //do nothing
    }

    default RenderComponent asComponent() {
        return new RenderComponent(this);
    }

    String getShaderKey();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Dto {
        private String clazz;
        private String shaderKey;
    }
}
