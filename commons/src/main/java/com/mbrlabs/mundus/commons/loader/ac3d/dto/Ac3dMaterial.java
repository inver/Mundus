package com.mbrlabs.mundus.commons.loader.ac3d.dto;

import com.mbrlabs.mundus.commons.dto.ColorDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Ac3dMaterial {
    private final String name;
    private final ColorDto diffuse;
    private final ColorDto ambient;
    private final ColorDto emissive;
    private final ColorDto specular;
    private final float shininess;
    private final float transparency;
}
