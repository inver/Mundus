package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

import java.util.HashMap;
import java.util.Map;

public class ShadersCtx {
    private final Map<String, BaseShader> shaders = new HashMap<>();

    public void addAll(Map<String, BaseShader> shaders) {
        this.shaders.putAll(shaders);
    }
}
