/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import net.nevinsky.mundus.core.Renderable;
import net.nevinsky.mundus.core.shader.Shader;

/**
 * @author Marcus Brummer
 * @version 08-01-2016
 */
public class SkyboxShader extends DefaultBaseShader {
    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_TEXTURE = register(new Uniform("u_texture"));
    protected final int UNIFORM_FOG = register(new Uniform("u_fog"));
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));

    private final Matrix4 transform = new Matrix4();

    public SkyboxShader(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        context.begin();
        program.bind();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        transform.idt();
        transform.translate(camera.position);
        set(UNIFORM_TRANS_MATRIX, transform);
    }

    @Override
    public void render(Renderable renderable) {
        // texture uniform
        var cubemapAttribute = ((CubemapAttribute) (
                renderable.getMaterial().get(CubemapAttribute.EnvironmentMap))
        );
        if (cubemapAttribute != null) {
            set(UNIFORM_TEXTURE, cubemapAttribute.textureDescription);
        }

        // Fog
        Fog fog = ((SceneEnvironment) renderable.getEnvironment()).getFog();
        if (fog == null) {
            set(UNIFORM_FOG, 0);
        } else {
            set(UNIFORM_FOG, 1);
            set(UNIFORM_FOG_COLOR, fog.color);
        }

        renderable.getMeshPart().render(program);
    }

    @Override
    public void end() {
        context.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }

}
