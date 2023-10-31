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

package com.mbrlabs.mundus.editor.ui.modules.inspector.model

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.ui.UiComponentHolder
import com.mbrlabs.mundus.editor.ui.modules.inspector.ComponentWidget
import com.mbrlabs.mundus.editor.ui.widgets.chooser.asset.AssetChooserField
import org.springframework.context.ApplicationContext

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
class ModelComponentWidget(
    uiComponentHolder: UiComponentHolder,
//    modelComponent: ModelComponent,
//    private val ctx: EditorCtx,
//    private val appUi: AppUi,
//    private val assetSelectionDialog: AssetPickerDialog,
//    private val assetManager: EditorAssetManager,
//    private val previewGenerator: PreviewGenerator,
    private val modelComponentPresenter: ModelWidgetPresenter,
    applicationContext: ApplicationContext
) : ComponentWidget(applicationContext) {

    private val materialContainer = VisTable()
    val assetField = AssetChooserField()

    init {
        setupUI()

        modelComponentPresenter.init(this)
    }

    private fun setupUI() {
        // create Model select dropdown
        addFormField("Asset", assetField, true)

        // create materials for all model nodes
        content.add(VisLabel("Materials")).expandX().fillX().left().padBottom(3f).padTop(3f).row()
        content.addSeparator().row()

        val label = VisLabel()
        label.wrap = true
        label.setText(
            "Here you change the materials of model components individually.\n"
                    + "Modifing the material will update all components, that use that material."
        )
        content.add(label).grow().padBottom(10f).row()

        content.add(materialContainer).grow().row()
        buildMaterials()
    }

    private fun buildMaterials() {
        materialContainer.clear()
//        for (g3dbMatID in component.materials.keys()) {
//
//            val mw = MaterialWidget(
//                ctx,
//                appUi,
//                assetSelectionDialog,
//                assetManager,
//                previewGenerator
//            )
//            mw.matChangedListener = object : MaterialWidget.MaterialChangedListener {
//                override fun materialChanged(materialAsset: MaterialAsset) {
//                    component.materials.put(g3dbMatID, materialAsset)
//                    component.applyMaterials()
//                }
//            }
//
//            mw.material = component.materials[g3dbMatID]
//            materialContainer.add(mw).grow().padBottom(20f).row()
//        }
    }

    override fun setValues(entityId: Int) {
        super.setValues(entityId)

        modelComponentPresenter.setValues(this)
    }

    fun resetValues(asset: Asset<*>) {
        assetField.setAsset(asset)
    }

}
