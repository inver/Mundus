package com.mbrlabs.mundus.commons.shaders;


import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

//todo migrate to ShaderProvider
public interface ShaderHolder {
    <T extends BaseShader> T get(String shaderKey);
}
