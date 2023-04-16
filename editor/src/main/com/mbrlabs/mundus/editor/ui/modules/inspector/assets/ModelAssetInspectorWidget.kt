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

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset
import com.mbrlabs.mundus.commons.assets.model.ModelAsset
import com.mbrlabs.mundus.editor.config.UiComponentHolder
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.PreviewGenerator
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget
import com.mbrlabs.mundus.editor.ui.widgets.MaterialWidget

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class ModelAssetInspectorWidget(
    private val ctx: EditorCtx,
    private val appUi: AppUi,
    private val assetManager: EditorAssetManager,
    private val assetSelectionDialog: AssetPickerDialog,
    private val toolManager: ToolManager,
    uiComponentHolder: UiComponentHolder,
    private val previewGenerator: PreviewGenerator
) : BaseInspectorWidget(uiComponentHolder, "Model Asset") {

    private var modelAsset: ModelAsset? = null

    // info
    private val name = VisLabel()
    private val nodeCount = VisLabel()
    private val materialCount = VisLabel()
    private val vertexCount = VisLabel()
    private val indexCount = VisLabel()

    // materials
    private val materialContainer = VisTable()

    // actions
    private val modelPlacement = VisTextButton("Activate model placement tool")

    init {
        isDeletable = false

        // info
        collapsibleContent.add(VisLabel("Info")).growX().row()
        collapsibleContent.addSeparator().padBottom(5f).row()
        collapsibleContent.add(name).growX().row()
        collapsibleContent.add(nodeCount).growX().row()
        collapsibleContent.add(materialCount).growX().row()
        collapsibleContent.add(vertexCount).growX().row()
        collapsibleContent.add(indexCount).growX().padBottom(15f).row()

        // actions
        collapsibleContent.add(VisLabel("Actions")).growX().row()
        collapsibleContent.addSeparator().padBottom(5f).row()
        collapsibleContent.add(modelPlacement).growX().padBottom(15f).row()

        // materials
        val label = VisLabel()
        label.setText(
            "Default model materials determine the initial materials a new model will get, if "
                    + "you use the model placement tool."
        )
        label.wrap = true
        collapsibleContent.add(VisLabel("Default model materials")).growX().row()
        collapsibleContent.addSeparator().padBottom(5f).row()
        collapsibleContent.add(label).padTop(4f).padBottom(15f).grow().row()
        collapsibleContent.add(materialContainer).growX().padBottom(15f).row()

        // model placement action
        modelPlacement.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                toolManager.modelPlacementTool.setModel(modelAsset)
                toolManager.activateTool(toolManager.modelPlacementTool)
            }
        })
    }

    private fun updateUI() {
        var verts = 0
        var indices = 0
        val model = modelAsset!!.model
        for (mesh in model.meshes) {
            verts += mesh.numVertices
            indices += mesh.numIndices
        }
        // set info
        name.setText("Name: " + modelAsset!!.name)
        nodeCount.setText("Nodes: " + model.nodes.size)
        //todo
//        materialCount.setText("Materials: " + model.materials.size)
        vertexCount.setText("Vertices: " + verts)
        indexCount.setText("Indices: " + indices)

        materialContainer.clear()
        for (g3dbMatID in modelAsset!!.defaultMaterials.keys) {
            val mat = modelAsset!!.defaultMaterials[g3dbMatID]
            val mw = MaterialWidget(
                ctx,
                appUi,
                assetSelectionDialog,
                assetManager,
                previewGenerator
            )
            mw.matChangedListener = object : MaterialWidget.MaterialChangedListener {
                override fun materialChanged(materialAsset: MaterialAsset) {
                    modelAsset!!.defaultMaterials.put(g3dbMatID, materialAsset)
                    modelAsset!!.applyDependencies()
                    toolManager.modelPlacementTool.setModel(modelAsset)
//                    assetManager.dirty(modelAsset!!)
                }
            }
            mw.material = mat
            materialContainer.add(mw).grow().padBottom(20f).row()
        }
    }

    fun setModel(model: ModelAsset) {
        this.modelAsset = model
        updateUI()
    }

    override fun onDelete() {
        // can't be deleted
    }

    override fun setValues(entityId: Int) {
        // nope
    }

}