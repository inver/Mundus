package com.mbrlabs.mundus.editor.core.shader;

import groovy.lang.GroovyClassLoader;
import lombok.Getter;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.ShaderWrapper;

public class EditorShaderWrapper extends ShaderWrapper {

    @Getter
    private final GroovyClassLoader shaderClassLoader;

    public EditorShaderWrapper(GroovyClassLoader shaderClassLoader, BaseShader instance) {
        super(instance);
        this.shaderClassLoader = shaderClassLoader;
    }
}
