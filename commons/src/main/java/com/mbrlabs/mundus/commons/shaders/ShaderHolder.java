package com.mbrlabs.mundus.commons.shaders;


import net.nevinsky.abyssus.core.shader.BaseShader;

//todo migrate to ShaderProvider
public interface ShaderHolder {
    <T extends BaseShader> T get(String shaderKey);
}
