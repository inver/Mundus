package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.RequiredArgsConstructor;
import net.nevinsky.mundus.core.shader.BaseShader;

@RequiredArgsConstructor
public abstract class DefaultBaseShader extends BaseShader {
    protected final String vertexShader;
    protected final String fragmentShader;

    protected void compile() {
        program = new ShaderProgram(vertexShader, fragmentShader);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
    }

    @Override
    public void init() {
        compile();
        init(program, null);
    }

}
