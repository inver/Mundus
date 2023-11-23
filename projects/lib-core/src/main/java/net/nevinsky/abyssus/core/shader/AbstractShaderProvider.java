package net.nevinsky.abyssus.core.shader;

import net.nevinsky.abyssus.core.Renderable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractShaderProvider<T extends ShaderHolder> implements ShaderProvider {

    protected final Map<String, T> shaderCache = new ConcurrentHashMap<>();

    @Override
    public Shader get(String key, Renderable renderable) {
        Shader res;
        if (renderable != null) {
            res = renderable.shader;
            if (res != null && res.canRender(renderable)) {
                return res;
            }
        }

        res = getShaderFromCache(shaderCache, key, renderable);
        return res;
    }

    protected Shader getShaderFromCache(Map<String, T> cacheMap, String key, Renderable renderable) {
        var holderByKey = cacheMap.computeIfAbsent(key, k -> createHolder(key));

        if (holderByKey == null) {
            return null;
        }
        var res = holderByKey.getForRenderable(renderable);
        if (res != null) {
            return res;
        }

        res = createShader(holderByKey, renderable);
        if (res != null && res.canRender(renderable)) {
            res.init(renderable);
            holderByKey.addShader(res);
            return res;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected T createHolder(String key) {
        return (T) new ShaderHolder(key);
    }

    protected abstract Shader createShader(T holder, Renderable renderable);

    @Override
    public void dispose() {
        shaderCache.forEach((key, value) -> value.dispose());
    }
}
