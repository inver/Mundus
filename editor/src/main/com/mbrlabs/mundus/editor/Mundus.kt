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

package com.mbrlabs.mundus.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.kotcrab.vis.ui.VisUI
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.utils.Fa

/**
 * Core class.
 *
 * Used for dependency injection of core components and as event bus.
 *
 * @author Marcus Brummer
 * @version 08-12-2015
 */
object Mundus {

    lateinit var fa: BitmapFont

    init {
        initFontAwesome()
    }

    private fun initFontAwesome() {
        val faBuilder = Fa(Gdx.files.internal("fonts/fa45.ttf"))
        faBuilder.generatorParameter.size = (Gdx.graphics.height * 0.02f).toInt()
        faBuilder.generatorParameter.kerning = true
        faBuilder.generatorParameter.borderStraight = false
        fa = faBuilder.addIcon(Fa.SAVE).addIcon(Fa.DOWNLOAD).addIcon(Fa.GIFT).addIcon(Fa.PLAY).addIcon(Fa.MOUSE_POINTER)
            .addIcon(Fa.ARROWS).addIcon(Fa.CIRCLE_O).addIcon(Fa.CIRCLE).addIcon(Fa.MINUS).addIcon(Fa.CARET_DOWN)
            .addIcon(Fa.CARET_UP).addIcon(Fa.TIMES).addIcon(Fa.SORT).addIcon(Fa.HASHTAG).addIcon(Fa.PAINT_BRUSH)
            .addIcon(Fa.STAR).addIcon(Fa.REFRESH).addIcon(Fa.EXPAND).build()
    }

    /**
     * Disposes everything.
     */
    fun dispose() {
        VisUI.dispose()
        Shaders.dispose()
        fa.dispose()
    }

}
