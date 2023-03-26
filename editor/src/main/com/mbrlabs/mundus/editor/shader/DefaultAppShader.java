package com.mbrlabs.mundus.editor.shader;


import net.nevinsky.mundus.core.Renderable;
import net.nevinsky.mundus.core.shader.Shader;

public class DefaultAppShader extends AppBaseShader {
    public DefaultAppShader(String vertexShader, String fragmentShader) {
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
