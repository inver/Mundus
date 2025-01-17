package com.mbrlabs.mundus.commons.assets.shader;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShaderMeta {
    private String vertex;
    private String fragment;
    private String shaderClass;

    public ShaderMeta() {
        vertex = "shader.vert.gsls";
        fragment = "shader.frag.gsls";
    }
}
