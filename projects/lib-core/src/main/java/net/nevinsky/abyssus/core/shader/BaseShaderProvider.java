/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.Renderable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class BaseShaderProvider implements ShaderProvider {
    protected final Map<String, ShaderHolder> shaders = new ConcurrentHashMap<>();
    protected ShaderHolder defaultShader;

    protected void init() {
        defaultShader = loadShaderAndCache(DEFAULT_SHADER, null);
        if (defaultShader == null) {
            throw new GdxRuntimeException("Failed to load Default Shader!");
        }
    }

    @Override
    public Shader get(String key, Renderable renderable) {
        var suggestedShader = renderable.shader;
        if (suggestedShader != null && suggestedShader.canRender(renderable)) {
            return suggestedShader;
        }

        var res = loadShaderAndCache(key, renderable);
        if (res != null) {
            return res.defaultInstance;
        }

        return defaultShader.defaultInstance;
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseShader> T getInstance(ShaderHolder wrapper, Renderable renderable) {
        wrapper.init(renderable);
        return (T) wrapper.getDefaultInstance();
    }

    /**
     * Method load shader from file system or class path or etc. and put it to cache
     *
     * @param key the name of shader
     * @return cached holder with shader
     */
    protected abstract ShaderHolder loadShaderAndCache(String key, Renderable renderable);

    protected String createCompositeKey(String baseKey, Renderable renderable) {
        if (renderable == null) {
            return baseKey;
        }
        return baseKey + "_" + renderable.getRenderMask();
    }

    @Override
    public void dispose() {
        for (var holder : shaders.values()) {
            holder.dispose();
        }
        shaders.clear();
    }
}
