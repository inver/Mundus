package net.nevinsky.mundus.core.shader;

import com.badlogic.gdx.utils.Disposable;
import net.nevinsky.mundus.core.Renderable;

public interface ShaderProvider extends Disposable {
    Shader getShader(Renderable renderable);
}