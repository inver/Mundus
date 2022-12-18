package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.math.Quaternion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Vector4Dto {
    private final float x, y, z, w;

    public Vector4Dto(@JsonProperty("x") float x, @JsonProperty("y") float y, @JsonProperty("z") float z,
                      @JsonProperty("w") float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4Dto(Quaternion quaternion) {
        this(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public Quaternion toQuaternion() {
        return new Quaternion().mulLeft(x, y, z, w);
    }
}
