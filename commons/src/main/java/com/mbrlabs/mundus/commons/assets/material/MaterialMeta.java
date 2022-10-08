package com.mbrlabs.mundus.commons.assets.material;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialMeta {
    private String preview;

    private String diffuseTexture;
    private String ambientOcclusionTexture;
    private String albedoTexture;
    private String heightTexture;
    private String metallicTexture;
    private String normalTexture;
    private String roughnessTexture;

    private int diffuseColor;
    private float shininess;
    private float opacity;
}
