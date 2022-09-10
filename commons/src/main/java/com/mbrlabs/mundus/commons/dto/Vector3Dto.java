package com.mbrlabs.mundus.commons.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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
}
