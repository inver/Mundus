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

import com.badlogic.gdx.utils.Disposable;
import net.nevinsky.abyssus.core.Renderable;

/**
 * Returns {@link Shader} instances for a {@link Renderable} on request. Also responsible for disposing of any created
 * {@link Shader} instances on a call to {@link Disposable#dispose()}.
 *
 * @author badlogic
 */
public interface ShaderProvider extends Disposable {

    String DEFAULT_SHADER = "default";

    /**
     * Returns a {@link Shader} for the given {@link String}. The RenderInstance may already contain a Shader, in
     * which case the provider may decide to return that.
     *
     * @param key the key of shader
     * @return the Shader to be used for the RenderInstance
     */
    <T extends BaseShader> T get(String key);

    default DefaultShader get() {
        return get(DEFAULT_SHADER);
    }
}
