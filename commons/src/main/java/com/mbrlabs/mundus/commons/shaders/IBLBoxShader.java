package com.mbrlabs.mundus.commons.shaders;


import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.Shader;

public class IBLBoxShader extends BaseShader {
    @Override
    public void init() {

    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return false;
    }
}
