package com.mbrlabs.mundus.commons.shaders;

import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.shader.Shader;

public class AbyssusDefaultShader extends DefaultBaseShader {
    public AbyssusDefaultShader(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
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
