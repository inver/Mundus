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

package com.mbrlabs.mundus.editor.ui.modules.inspector

import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset
import com.mbrlabs.mundus.commons.assets.model.ModelAsset
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.PreviewGenerator
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.MaterialAssetInspectorWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.ModelAssetInspectorWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.TerrainAssetInspectorWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.TextureAssetInspectorWidget
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerPresenter

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class AssetInspector(
    separatorStyle: Separator.SeparatorStyle?,
    private val ctx: EditorCtx,
    private val appUi: AppUi,
    private val assetManager: EditorAssetManager,
    private val assetSelectionDialog: AssetPickerDialog,
    private val toolManager: ToolManager,
    private val projectManager: ProjectManager,
    private val previewGenerator: PreviewGenerator,
    private val colorPickerPresenter: ColorPickerPresenter
) : VisTable() {

    private val materialWidget = MaterialAssetInspectorWidget(
        separatorStyle,
        ctx,
        appUi,
        assetSelectionDialog,
        assetManager,
        previewGenerator,
        colorPickerPresenter
    )
    private val modelWidget = ModelAssetInspectorWidget(
        separatorStyle,
        ctx,
        appUi,
        assetManager,
        assetSelectionDialog,
        toolManager,
        previewGenerator
    )
    private val textureWidget = TextureAssetInspectorWidget(separatorStyle)
    private val terrainWidget = TerrainAssetInspectorWidget(separatorStyle)

    var asset: Asset<*>? = null
        set(value) {
            field = value
            clear()
            if (value is MaterialAsset) {
                add(materialWidget).growX().row()
                materialWidget.setMaterial(value)
            } else if (value is ModelAsset) {
                add(modelWidget).growX().row()
                modelWidget.setModel(value)
            } else if (value is TextureAsset) {
                add(textureWidget).growX().row()
                textureWidget.setTextureAsset(value)
            } else if (value is TerrainAsset) {
                add(terrainWidget).growX().row()
                terrainWidget.setTerrainAsset(value)
            }
        }

    init {
        align(Align.top)
        pad(7f)
    }

}
