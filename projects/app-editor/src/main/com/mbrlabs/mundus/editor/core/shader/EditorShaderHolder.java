package com.mbrlabs.mundus.editor.core.shader;

import groovy.lang.GroovyClassLoader;
import lombok.Getter;
import lombok.Setter;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.ShaderHolder;

public class EditorShaderHolder extends ShaderHolder {

    @Setter
    private BaseShader overrideInstance;

    @Getter
    private final GroovyClassLoader shaderClassLoader;

    public EditorShaderHolder(GroovyClassLoader shaderClassLoader, BaseShader instance) {
        super(instance);
        this.shaderClassLoader = shaderClassLoader;
    }

    @Override
    public BaseShader getDefaultInstance() {
        if (overrideInstance != null) {
            return overrideInstance;
        }
        return super.getDefaultInstance();
    }
}
