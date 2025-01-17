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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.settings

import com.badlogic.gdx.utils.JsonWriter
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.mbrlabs.mundus.editor.ui.UiConstants
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
class ExportSettingsTable : BaseSettingsTable() {

    val fileChooserField = FileChooserField(500)
    val jsonType = VisSelectBox<JsonWriter.OutputType>()
    val allAssets = VisCheckBox("Export unused assets [will be ignored for now]")
    val compression = VisCheckBox("Compress scenes [will be ignored for now]")

    init {
        top().left()
        padRight(UiConstants.PAD_SIDE).padLeft(UiConstants.PAD_SIDE)

        jsonType.setItems(
            JsonWriter.OutputType.javascript,
            JsonWriter.OutputType.json,
            JsonWriter.OutputType.minimal
        )

        add(VisLabel("Export Settings")).left().row()
        addSeparator().padBottom(UiConstants.PAD_SIDE * 2)

        add(VisLabel("Output folder")).growX().row()
        add(fileChooserField).growX().padBottom(UiConstants.PAD_BOTTOM).row()
        add(VisLabel("Scene json format")).growX().row()
        add(jsonType).growX().padBottom(UiConstants.PAD_BOTTOM).row()
        add(VisLabel("Flags")).growX().row()
        add(allAssets).left().row()
        add(compression).left().row()

        fileChooserField.setFileMode(FileChooser.SelectionMode.DIRECTORIES)
    }
}