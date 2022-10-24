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

import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.mbrlabs.mundus.editor.core.registry.KeyboardLayout
import com.mbrlabs.mundus.editor.ui.UiConstants
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField

/**
 * @author Marcus Brummer
 * @version 29-02-2016
 */
class GeneralSettingsTable : BaseSettingsTable() {

    val fbxBinary = FileChooserField(500)
    val keyboardLayouts = VisSelectBox<KeyboardLayout>()
    val autoReloadFromDisk = VisCheckBox("Auto reload from disk");

    init {
        top().left()
        padRight(UiConstants.PAD_SIDE).padLeft(UiConstants.PAD_SIDE)

        add(VisLabel("General Settings")).left().row()
        addSeparator().padBottom(UiConstants.PAD_SIDE * 2)
        add(VisLabel("fbx-conv binary")).left().row()
        add(fbxBinary).growX().padBottom(UiConstants.PAD_BOTTOM).row()

        keyboardLayouts.setItems(KeyboardLayout.QWERTY, KeyboardLayout.QWERTZ)

        add(VisLabel("Keyboard Layout")).growX().row()
        add(keyboardLayouts).growX().row()

        add(autoReloadFromDisk).row();
    }
}
