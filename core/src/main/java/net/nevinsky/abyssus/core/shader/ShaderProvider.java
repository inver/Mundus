package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.utils.Disposable;
import net.nevinsky.abyssus.core.Renderable;

public interface ShaderProvider extends Disposable {
    Shader getShader(Renderable renderable);
}