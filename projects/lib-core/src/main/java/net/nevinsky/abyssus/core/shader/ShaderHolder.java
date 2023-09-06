package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.Renderable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ShaderHolder implements Disposable {

    @Getter
    protected final String key;
    protected final List<Shader> shaders = new ArrayList<>();

    public Shader getForRenderable(Renderable renderable) {
        for (var shader : shaders) {
            if (shader.canRender(renderable)) {
                return shader;
            }
        }
        return null;
    }

    public void addShader(Shader shader) {
        shaders.add(shader);
    }

    @Override
    public void dispose() {
        shaders.forEach(Disposable::dispose);
        shaders.clear();
    }
}
