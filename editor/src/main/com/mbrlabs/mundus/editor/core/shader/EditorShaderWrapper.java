package com.mbrlabs.mundus.editor.core.shader;

import groovy.lang.GroovyClassLoader;
import lombok.Getter;
import lombok.Setter;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.ShaderWrapper;

public class EditorShaderWrapper extends ShaderWrapper {

    @Setter
    private BaseShader overrideInstance;

    @Getter
    private final GroovyClassLoader shaderClassLoader;

    public EditorShaderWrapper(GroovyClassLoader shaderClassLoader, BaseShader instance) {
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
