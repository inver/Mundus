package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.utils.GdxRuntimeException;
import net.nevinsky.abyssus.core.Renderable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseShaderProvider implements ShaderProvider {
    protected List<Shader> shaders = new ArrayList<>();

    @Override
    public Shader getShader(Renderable renderable) {
        Shader suggestedShader = renderable.getShader();
        if (suggestedShader != null && suggestedShader.canRender(renderable)) return suggestedShader;
        for (Shader shader : shaders) {
            if (shader.canRender(renderable)) {
                return shader;
            }
        }
        final Shader shader = createShader(renderable);
        if (!shader.canRender(renderable)) {
            throw new GdxRuntimeException("unable to provide a shader for this renderable");
        }
        shader.init();
        shaders.add(shader);
        return shader;
    }

    protected abstract Shader createShader(final Renderable renderable);

    @Override
    public void dispose() {
        for (Shader shader : shaders) {
            shader.dispose();
        }
        shaders.clear();
    }
}