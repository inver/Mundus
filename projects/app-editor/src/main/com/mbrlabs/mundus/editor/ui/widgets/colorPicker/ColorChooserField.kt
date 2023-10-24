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

package com.mbrlabs.mundus.editor.ui.widgets.colorPicker

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.kotcrab.vis.ui.widget.color.ColorPickerListener

/**
 * An un-editable text field with a color picker.
 *
 * The text field shows the color hex value, the button launches a color picker dialog.
 *
 * @author Marcus Brummer
 * @version 08-01-2016
 */
class ColorChooserField : VisTable() {

    /**
     * The currently selected color.
     */
    var selectedColor: Color = Color.WHITE.cpy()
        set(value) {
            field.set(value)
            textField.text = "#$value"
        }

    /**
     * An optional color picker listener.
     * Will be called if user changed color.
     */
    var colorAdapter: ColorPickerAdapter? = null

    private val textField: VisTextField = VisTextField()

    val colorPickerListenerInternal: ColorPickerListener
    val cpBtn: VisTextButton = VisTextButton("Select")

    init {
        // setup internal color picker listener
        colorPickerListenerInternal = object : ColorPickerListener {
            override fun canceled(oldColor: Color?) {
                colorAdapter?.canceled(oldColor)
            }

            override fun reset(previousColor: Color?, newColor: Color?) {
                colorAdapter?.reset(previousColor, newColor)
            }

            override fun changed(newColor: Color?) {
                colorAdapter?.changed(newColor)
            }

            override fun finished(newColor: Color) {
                selectedColor = newColor
                colorAdapter?.finished(newColor)
            }
        }

        textField.isDisabled = true

        add(textField).padRight(5f).fillX().expandX()
        add(cpBtn).row()
    }

    /**
     * Disables the button for the color picker.
     */
    fun disable(disable: Boolean) {
        cpBtn.isDisabled = disable
        if (disable) {
            cpBtn.touchable = Touchable.disabled
        } else {
            cpBtn.touchable = Touchable.enabled
        }
    }
}
