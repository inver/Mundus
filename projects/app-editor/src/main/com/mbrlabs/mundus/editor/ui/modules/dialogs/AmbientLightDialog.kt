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

package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerField
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerPresenter
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * *
 * @version 04-03-2016
 */
@Component
class AmbientLightDialog(
    eventBus: EventBus,
    colorPickerPresenter: ColorPickerPresenter,
    private val ctx: EditorCtx
) : BaseDialog("Ambient Light"),
    ProjectChangedEvent.ProjectChangedListener,
    SceneChangedEvent.SceneChangedListener {

    private val intensity = VisTextField("0")
    private val colorPickerField = ColorPickerField()

    init {
        eventBus.register(this)

        colorPickerPresenter.init(colorPickerField)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val root = Table()
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        root.add(VisLabel("Intensity: ")).left().padBottom(10f)
        root.add(intensity).fillX().expandX().padBottom(10f).row()
        root.add(VisLabel("Color")).growX().row()
        root.add(colorPickerField).left().fillX().expandX().colspan(2).row()
//        resetValues()
    }

    private fun setupListeners() {
        // intensity
        intensity.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val d = convert(intensity.text)
                if (d != null) {
//                    ctx.current.currentScene.environment.ambientLight.intensity = d
                }
            }
        })

        // color
        colorPickerField.colorAdapter = object : ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                ctx.current.currentScene.environment.set(ColorAttribute(ColorAttribute.AmbientLight, color))
            }
        }

    }

    private fun resetValues() {
        val light = ctx.current.currentScene.environment.get(ColorAttribute.AmbientLight) as ColorAttribute
        //todo
//        intensity.text = light.intensity.toString()
        colorPickerField.color = light.color
    }

    private fun convert(input: String): Float? {
        try {
            if (input.isEmpty()) return null
            return java.lang.Float.valueOf(input)
        } catch (e: Exception) {
            return null
        }

    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        resetValues()
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        resetValues()
    }

}
