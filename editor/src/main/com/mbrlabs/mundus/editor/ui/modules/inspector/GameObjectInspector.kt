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
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.config.UiWidgetsHolder
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.PreviewGenerator
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.ComponentWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.IdentifierWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.TransformWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.TerrainWidgetPresenter
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerPresenter

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class GameObjectInspector(
    private val ctx: EditorCtx,
    private val appUi: AppUi,
    private val uiWidgetsHolder: UiWidgetsHolder,
    private val assetPickerDialog: AssetPickerDialog,
    private val assetManager: EditorAssetManager,
    private val history: CommandHistory,
    private val terrainWidgetPresenter: TerrainWidgetPresenter,
    private val previewGenerator: PreviewGenerator,
    private val colorPickerPresenter: ColorPickerPresenter
) : VisTable() {

    private val identifierWidget = IdentifierWidget(ctx)
    private val transformWidget = TransformWidget(uiWidgetsHolder.separatorStyle, ctx)
    private val componentWidgets: Array<ComponentWidget> = Array()
    private val addComponentBtn = VisTextButton("Add Component")
    private val componentTable = VisTable()

    private var entityId = -1

    init {
        align(Align.top)
        add(identifierWidget).growX().pad(7f).row()
        add(transformWidget).growX().pad(7f).row()
        for (cw in componentWidgets) {
            componentTable.add<BaseInspectorWidget>(cw).row()
        }
        add(componentTable).growX().pad(7f).row()
        add(addComponentBtn).expandX().fill().top().center().pad(10f).row()
    }

    fun setEntity(entityId: Int) {
        this.entityId = entityId

        // build ui
        buildComponentWidgets()
        componentTable.clearChildren()
        for (cw in componentWidgets) {
            componentTable.add(cw).grow().row()
        }

        // update
        updateGameObject()
    }

    fun updateGameObject() {
        if (entityId < 0) {
            return
        }

        identifierWidget.setValues(ctx.currentWorld.getEntity(entityId).isActive, "TODO add name here")
        transformWidget.setValues(entityId)

//        for (cw in componentWidgets) {
//            cw.setValues(gameObject!!)
//        }
    }

    private fun buildComponentWidgets() {
//        if (gameObject != null) {
//            componentWidgets.clear()
//            for (component in gameObject!!.components) {
//                // model component widget!!
//                if (component.type == Component.Type.MODEL) {
//                    componentWidgets.add(
//                        ModelComponentWidget(
//                            uiWidgetsHolder.separatorStyle,
//                            component as ModelComponent,
//                            ctx,
//                            appUi,
//                            assetPickerDialog,
//                            assetManager,
//                            previewGenerator
//                        )
//                    )
//                    // terrainAsset component widget
//                } else if (component.type == Component.Type.TERRAIN) {
//                    componentWidgets.add(
//                        TerrainComponentWidget(
//                            uiWidgetsHolder.separatorStyle,
//                            component as TerrainComponent,
//                            terrainWidgetPresenter
//                        )
//                    )
//                } else if (component.type == Component.Type.LIGHT) {
//                    componentWidgets.add(
//                        DirectionalLightComponentWidget(
//                            uiWidgetsHolder.separatorStyle,
//                            component as DirectionalLightComponent,
//                            colorPickerPresenter
//                        )
//                    )
//                }
//            }
//        }
    }

}
