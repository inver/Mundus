/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
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
    protected final Map<String, Shader> shaders = new ConcurrentHashMap<>();

    @Override
    public <T extends BaseShader> T get(String key) {
        var res = shaders.get(key);
        if (res != null) {
            return (T) res;
        }

        res = loadShader(key);
        if (res != null) {
            res.init();
            shaders.put(key, res);
        }
        return (T) res;
    }

    protected abstract BaseShader loadShader(String key);

    @Override
    public void dispose() {
        for (Shader shader : shaders.values()) {
            shader.dispose();
        }
        shaders.clear();
    }
}
