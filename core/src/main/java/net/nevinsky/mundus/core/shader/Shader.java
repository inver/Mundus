package net.nevinsky.mundus.core.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.Disposable;
import net.nevinsky.mundus.core.Renderable;

public interface Shader extends Disposable {
    void init();

    int compareTo(Shader other); // TODO: probably better to add some weight value to sort on

    boolean canRender(Renderable instance);

    void begin(Camera camera, RenderContext context);

    void render(final Renderable renderable);

    void end();
}
