package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.RequiredArgsConstructor;

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
