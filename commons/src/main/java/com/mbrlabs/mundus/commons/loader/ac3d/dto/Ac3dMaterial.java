package com.mbrlabs.mundus.commons.loader.ac3d.dto;

import com.mbrlabs.mundus.commons.dto.ColorDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Ac3dMaterial {
    private final String name;
    private final ColorDTO diffuse;
    private final ColorDTO ambient;
    private final ColorDTO emissive;
    private final ColorDTO specular;
    private final float shininess;
    private final float transparency;
}
