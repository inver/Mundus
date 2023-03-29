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

package com.mbrlabs.mundus.editor.ui.modules.inspector.assets

import com.mbrlabs.mundus.commons.assets.material.MaterialAsset
import com.mbrlabs.mundus.editor.config.UiComponentHolder
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.PreviewGenerator
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget
import com.mbrlabs.mundus.editor.ui.widgets.MaterialWidget
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerPresenter

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class MaterialAssetInspectorWidget(
    private val ctx: EditorCtx,
    private val appUi: AppUi,
    private val assetSelectionDialog: AssetPickerDialog,
    private val assetManager: EditorAssetManager,
    private val previewGenerator: PreviewGenerator,
    private val uiComponentHolder: UiComponentHolder,
    colorPickerPresenter: ColorPickerPresenter
) : BaseInspectorWidget(uiComponentHolder, "Material Asset") {

    private var material: MaterialAsset? = null
    private val materialWidget =
        MaterialWidget(ctx, appUi, assetSelectionDialog, assetManager, previewGenerator)

    init {
        colorPickerPresenter.init(materialWidget.diffuseColorField)

        isDeletable = false
        collapsibleContent.add(materialWidget).grow().row()
    }

    fun setMaterial(material: MaterialAsset) {
        this.material = material
        materialWidget.material = this.material
    }

    override fun onDelete() {
        // can't be deleted
    }

    override fun setValues(entityId: Int) {
        // nope
    }

}
