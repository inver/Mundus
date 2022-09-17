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

package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent
import com.mbrlabs.mundus.editor.assets.AssetMaterialFilter
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.PreviewGenerator
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerField

/**
 * Displays all properties of a material.
 *
 * You can also edit materials and replace them with another materials by.
 *
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class MaterialWidget(
    private val ctx: EditorCtx,
    private val appUi: AppUi,
    private val assetSelectionDialog: AssetPickerDialog,
    private val assetManager: EditorAssetManager,
    private val previewGenerator: PreviewGenerator
) : VisTable() {

    private val matFilter: AssetMaterialFilter = AssetMaterialFilter()
    private val matChangedBtn: VisTextButton = VisTextButton("change")
    private val matPickerListener: AssetPickerDialog.AssetPickerListener

    private val matNameLabel: VisLabel = VisLabel()
    val diffuseColorField: ColorPickerField = ColorPickerField()
    private val diffuseAssetField: AssetSelectionField = AssetSelectionField(assetSelectionDialog)
    private val shininessField = VisTextField()

    private val previewWidgetContainer = VisTable()

    /**
     * The currently active material of the widget.
     */
    var material: MaterialAsset? = null
        set(value) {
            if (value != null) {
                field = value
                diffuseColorField.selectedColor = value.diffuseColor
                diffuseAssetField.setAsset(value.diffuseTexture)
                matNameLabel.setText(value.name)
                shininessField.text = value.shininess.toString()

                previewWidgetContainer.clearChildren()
                previewWidgetContainer.add(previewGenerator.createPreviewWidget(appUi, value)).expand().fill()
            }
        }

    /**
     * An optional listener for changing the material. If the property is null
     * the user will not be able to change the material.
     */
    var matChangedListener: MaterialChangedListener? = null
        set(value) {
            field = value
            matChangedBtn.touchable = if (value == null) Touchable.disabled else Touchable.enabled
        }

    init {
        align(Align.topLeft)
        matNameLabel.setWrap(true)

        matPickerListener = object : AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material = asset as? MaterialAsset
                matChangedListener?.materialChanged(material!!)
            }
        }

        setupWidgets()
    }

    private fun setupWidgets() {
        previewWidgetContainer.debugAll()
        add(previewWidgetContainer).height(250f).width(250f).row()

        val table = VisTable()
        table.add(matNameLabel).grow()
        table.add(matChangedBtn).padLeft(4f).right().row()
        add(table).grow().row()

        addSeparator().growX().row()

        add(VisLabel("Diffuse texture")).grow().row()
        add(diffuseAssetField).growX().row()
        add(VisLabel("Diffuse color")).grow().row()
        add(diffuseColorField).growX().row()
        add(VisLabel("Shininess")).growX().row()
        add(shininessField).growX().row()

        matChangedBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                assetSelectionDialog.show(false, matFilter, matPickerListener)
            }
        })

        // diffuse texture
        diffuseAssetField.assetFilter = AssetTextureFilter()
        diffuseAssetField.pickerListener = object : AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material?.diffuseTexture = asset as? TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                assetManager.dirty(material!!)
            }
        }

        // diffuse color
        diffuseColorField.colorAdapter = object : ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                material?.diffuseColor?.set(newColor)
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                assetManager.dirty(material!!)
            }
        }

        // shininess
        shininessField.textFieldFilter = FloatDigitsOnlyFilter(false)
        shininessField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (shininessField.isInputValid && !shininessField.isEmpty) {
                    material?.shininess = shininessField.text.toFloat()
                    applyMaterialToModelAssets()
                    applyMaterialToModelComponents()
                    assetManager.dirty(material!!)
                }
            }
        })

    }

    // TODO find better solution than iterating through all components
    private fun applyMaterialToModelComponents() {
        val sceneGraph = ctx.current.currentScene.sceneGraph
        for (go in sceneGraph.gameObjects) {
            val mc = go.findComponentByType(Component.Type.MODEL)
            if (mc != null && mc is ModelComponent) {
                mc.applyMaterials()
            }
        }
    }

    // TODO find better solution than iterating through all assets
    private fun applyMaterialToModelAssets() {
        for (modelAsset in assetManager.assets) {
            modelAsset.applyDependencies()
        }
    }

    /**
     *
     */
    interface MaterialChangedListener {
        fun materialChanged(materialAsset: MaterialAsset)
    }


}
