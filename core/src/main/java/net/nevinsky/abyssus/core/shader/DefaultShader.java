package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.Renderable;

@RequiredArgsConstructor
public class DefaultShader extends BaseShader {
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

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return false;
    }
}
