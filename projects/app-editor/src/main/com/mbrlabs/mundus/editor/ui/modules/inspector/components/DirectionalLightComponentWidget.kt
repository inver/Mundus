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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.editor.ui.UiComponentHolder
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerField
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerPresenter

/**
 * @author Guilherme Nemeth
 * @version 07-07-2017
 */
class DirectionalLightComponentWidget(
    uiComponentHolder: UiComponentHolder,
    entityId: Int,
    colorPickerPresenter: ColorPickerPresenter
) : ComponentWidget(uiComponentHolder, "Directional Light Component", entityId) {

    private val FIELD_SIZE = 65
    private val dirX = FloatFieldWithLabel("x", FIELD_SIZE)
    private val dirY = FloatFieldWithLabel("y", FIELD_SIZE)
    private val dirZ = FloatFieldWithLabel("z", FIELD_SIZE)

    val colorField: ColorPickerField = ColorPickerField()
    private val intensityField: VisTextField = VisTextField()

    init {
        isDeletable = false

        colorPickerPresenter.init(colorField)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val pad = 4
        val dirWrapper = VisTable()
        dirWrapper.add(VisLabel("Direction: ")).padBottom(pad.toFloat()).grow()
        dirWrapper.add(dirX).padBottom(pad.toFloat())
        dirWrapper.add(dirY).padBottom(pad.toFloat())
        dirWrapper.add(dirZ).padBottom(pad.toFloat()).row()

        collapsibleContent.add(dirWrapper).grow().row()
        collapsibleContent.addSeparator().row()
        collapsibleContent.add(VisLabel("Color")).grow().left().row()
        collapsibleContent.add(colorField).grow().left().row()
        collapsibleContent.add(VisLabel("Intensity")).grow().row()
        collapsibleContent.add(intensityField).growX().row()
    }

    private fun setupListeners() {
//        dirX.addListener(object : ChangeListener() {
//            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                component.directionalLight.direction.x = dirX.float
//            }
//        })
//        dirY.addListener(object : ChangeListener() {
//            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                component.directionalLight.direction.y = dirY.float
//            }
//        })
//        dirZ.addListener(object : ChangeListener() {
//            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                component.directionalLight.direction.z = dirZ.float
//            }
//        })
//
//        colorField.colorAdapter = object : ColorPickerAdapter() {
//            override fun finished(newColor: Color) {
//                component.directionalLight.color.set(newColor)
//            }
//        }
//
//        intensityField.textFieldFilter = FloatDigitsOnlyFilter(false)
//        intensityField.addListener(object : ChangeListener() {
//            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                if (!intensityField.isEmpty && intensityField.isInputValid) {
//                    component.directionalLight.intensity = intensityField.text.toFloat()
//                }
//            }
//        })
    }

    override fun setValues(entityId: Int) {
//        val c = go.findComponentByType(Component.Type.LIGHT)
//        if (c != null) {
//            component = c as DirectionalLightComponent
//            val light = component.directionalLight
//
//            dirX.text = formatFloat(light.direction.x, 2)
//            dirY.text = formatFloat(light.direction.y, 2)
//            dirZ.text = formatFloat(light.direction.z, 2)
//
//            colorField.selectedColor = light.color
//            intensityField.text = light.intensity.toString()
//        }
    }
}
