package com.mbrlabs.mundus.commons.dto.vertex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VertexDto {
    private final float x, y, z;

    @Override
    public String toString() {
        return String.format("Vertex(%s, %s, %s)", x, y, z);
    }
}
