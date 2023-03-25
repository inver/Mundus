package net.nevinsky.mundus.core;

import com.badlogic.gdx.utils.Pool;

import java.util.List;

public interface RenderableProvider {
    void getRenderables(List<Renderable> renderables, Pool<Renderable> pool);
}
