package com.mbrlabs.mundus.commons.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ColorDTO {
    private final float r, g, b, a;

    public ColorDTO(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        a = 1f;
    }
}
