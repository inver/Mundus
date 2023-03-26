package com.mbrlabs.mundus.commons.shaders;


import net.nevinsky.mundus.core.shader.BaseShader;

public interface ShaderHolder {
    <T extends BaseShader> T get(String shaderKey);
}
