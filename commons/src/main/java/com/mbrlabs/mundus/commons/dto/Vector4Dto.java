package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.math.Quaternion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Vector4Dto {
    private final float x, y, z, w;

    public Vector4Dto(Quaternion quaternion) {
        this(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public Quaternion toQuaternion() {
        return new Quaternion().mulLeft(x, y, z, w);
    }
}
