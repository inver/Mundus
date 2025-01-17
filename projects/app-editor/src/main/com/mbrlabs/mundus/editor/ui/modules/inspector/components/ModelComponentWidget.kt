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
import com.mbrlabs.mundus.editor.ui.UiComponentHolder

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
    entityId: Int
) : ComponentWidget(uiComponentHolder, "Model Component", entityId) {

    private val materialContainer = VisTable()

    init {
        setupUI()
    }

    private fun setupUI() {
        // create Model select dropdown
        collapsibleContent.add(VisLabel("Model")).left().row()
        collapsibleContent.addSeparator().padBottom(5f).row()
        //collapsibleContent.add(selectBox).expandX().fillX().row();
//        collapsibleContent.add(VisLabel("Model asset: " + component.modelAsset.name)).grow().padBottom(15f).row()

        // create materials for all model nodes
        collapsibleContent.add(VisLabel("Materials")).expandX().fillX().left().padBottom(3f).padTop(3f).row()
        collapsibleContent.addSeparator().row()

        val label = VisLabel()
        label.wrap = true
        label.setText(
            "Here you change the materials of model components individually.\n"
                    + "Modifing the material will update all components, that use that material."
        )
        collapsibleContent.add(label).grow().padBottom(10f).row()

        collapsibleContent.add(materialContainer).grow().row()
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
//        val c = go.findComponentByType(Component.Type.MODEL)
//        if (c != null) {
//            component = c as ModelComponent
//        }
    }

}
