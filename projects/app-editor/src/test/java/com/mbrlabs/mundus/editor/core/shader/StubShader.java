package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.shader.DefaultShader;
import net.nevinsky.abyssus.core.shader.ShaderConfig;

@Getter
public class StubShader extends DefaultShader {

    private final String vertex;
    private final String fragment;

    public StubShader(ShaderConfig config, Renderable renderable) {
        super(config, renderable);
        vertex = config.getVertexShader();
        fragment = config.getFragmentShader();
    }

    @Override
    public void init(Renderable renderable) {

    }

    @Override
    public void begin(Camera camera, RenderContext context) {

    }

    @Override
    public void render(Renderable renderable) {

    }

    @Override
    public void end() {

    }

    @Override
    public void dispose() {

    }
}
