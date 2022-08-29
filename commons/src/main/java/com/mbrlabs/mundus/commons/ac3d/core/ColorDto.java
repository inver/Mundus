package com.mbrlabs.mundus.commons.ac3d.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ColorDto {
    private final float r, g, b, a;

    public ColorDto(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        a = 1f;
    }
}
