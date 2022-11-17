package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Vector3Dto {
    private final float x, y, z;

    public Vector3Dto(float[] floatArray) {
        if (floatArray == null || floatArray.length != 3) {
            throw new IllegalArgumentException();
        }

        x = floatArray[0];
        y = floatArray[1];
        z = floatArray[2];
    }

    @JsonCreator
    public Vector3Dto(@JsonProperty("x") float x, @JsonProperty("y") float y, @JsonProperty("z") float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3Dto(Vector3 vector3) {
        x = vector3.x;
        y = vector3.y;
        z = vector3.z;
    }

    public Vector3 toVector() {
        return new Vector3(x, y, z);
    }
}
