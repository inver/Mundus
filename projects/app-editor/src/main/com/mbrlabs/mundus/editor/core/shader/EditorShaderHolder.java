package com.mbrlabs.mundus.editor.core.shader;

import lombok.Getter;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.ShaderHolder;

public class EditorShaderHolder extends ShaderHolder {

    @Getter
    private final Class<?> shaderClass;

    public EditorShaderHolder(String key, Class<?> shaderClass) {
        super(key);
        this.shaderClass = shaderClass;
    }

}
