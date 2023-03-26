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

package com.mbrlabs.mundus.editor.shader

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.mbrlabs.mundus.commons.shaders.DefaultBaseShader
import com.mbrlabs.mundus.editor.utils.GlUtils
import net.nevinsky.mundus.core.Renderable
import net.nevinsky.mundus.core.shader.Shader

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
class WireframeShader(vertex: String, fragment: String) : DefaultBaseShader(vertex, fragment) {

    private val UNIFORM_PROJ_VIEW_MATRIX = register(Uniform("u_projViewMatrix"))
    private val UNIFORM_TRANS_MATRIX = register(Uniform("u_transMatrix"))

    override fun compareTo(other: Shader): Int {
        return 0
    }

    override fun canRender(instance: Renderable): Boolean {
        return true
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.context = context
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)

        program.bind()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
    }

    override fun render(renderable: Renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)
        GlUtils.Unsafe.polygonModeWireframe()

        renderable.meshPart.render(program)
    }

    override fun end() {
        GlUtils.Unsafe.polygonModeFill()
    }

    override fun dispose() {
        program.dispose()
    }

}
