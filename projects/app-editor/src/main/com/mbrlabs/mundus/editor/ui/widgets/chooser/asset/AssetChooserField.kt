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

package com.mbrlabs.mundus.editor.ui.widgets.chooser.asset

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.assets.AssetFilter
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerListener

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class AssetChooserField : VisTable() {

    private val textField = VisTextField()
    val selectButton = VisTextButton("Select")

    var pickerListener: AssetPickerListener? = null
    var assetFilter: AssetFilter? = null

    init {
        add(textField).growX()
        textField.isDisabled = true
        add(selectButton).padLeft(8f).row()
    }

    fun setAsset(asset: Asset<*>?) {
        textField.text = if (asset == null) "None" else asset.name
    }

    open fun disable(disable: Boolean) {
        selectButton.isDisabled = disable
        if (disable) {
            selectButton.touchable = Touchable.disabled
        } else {
            selectButton.touchable = Touchable.enabled
        }
    }
}
