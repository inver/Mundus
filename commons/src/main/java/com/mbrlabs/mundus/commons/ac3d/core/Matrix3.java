package com.mbrlabs.mundus.commons.ac3d.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Matrix3 {
    private final float m00, m01, m02, m10, m11, m12, m20, m21, m22;
}
