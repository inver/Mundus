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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseShaderProvider implements ShaderProvider {
    protected final Map<String, ShaderWrapper> shaders = new ConcurrentHashMap<>();

    @Override
    public <T extends BaseShader> T get(String key) {
        var res = shaders.get(key);
        if (res != null) {
            return getInstance(res);
        }

        res = loadShader(key);
        if (res != null) {
            shaders.put(key, res);
            return getInstance(res);
        }

        var wrapper = shaders.get(DEFAULT_SHADER);
        if (wrapper == null) {
            throw new RuntimeException(String.format("Shader with name '%s' is null", key));
        }
        return getInstance(wrapper);
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseShader> T getInstance(ShaderWrapper wrapper) {
        wrapper.init();
        return (T) wrapper.getInstance();
    }

    protected abstract ShaderWrapper loadShader(String key);

    @Override
    public void dispose() {
        for (var wrapper : shaders.values()) {
            wrapper.dispose();
        }
        shaders.clear();
    }


}
