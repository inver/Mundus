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
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.ui.dsl.UiDslCreator

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class GameObjectInspector(uiDslCreator: UiDslCreator) : VisTable() {

    private val dlsWidget =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/identifier/IdentifierWidget.groovy");
    private val transformWidget =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/transform/TransformWidget.groovy");
    private val terrainComponentWidgetDsl =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/terrain/TerrainWidget.groovy");
    private val modelComponentWidgetDsl =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/model/ModelWidget.groovy");

    private val componentWidgets = ArrayList<ComponentWidget>()
    private val addComponentBtn = VisTextButton("Add Component")
    private val componentTable = VisTable()

    private var entityId = -1

    init {
        align(Align.top)
        add(dlsWidget.actor).growX().pad(8f).row()
        add(transformWidget.actor).growX().pad(8f).row()
        add(terrainComponentWidgetDsl.actor).growX().pad(8f).row()
//        add(materialComponentWidgetDsl.actor).growX().pad(8f).row()
        add(modelComponentWidgetDsl.actor).growX().pad(8f).row()

        componentWidgets.forEach { componentTable.add<BaseInspectorWidget>(it).row() }

        add(componentTable).growX().pad(8f).row()
//        add(addComponentBtn).expandX().fill().top().center().pad(10f).row()
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

//        transformWidget.setValues(entityId)
        componentWidgets.forEach { it.setValues(entityId) }
    }

    private fun buildComponentWidgets() {
        if (entityId < 0) {
            return
        }

        componentWidgets.clear()
//        val component = ctx.currentWorld.getEntity(entityId).getComponent(TypeComponent::class.java) ?: return

        // todo reuse created components, instead of creating on each entity select
//        if (component.type == TypeComponent.Type.LIGHT_DIRECTIONAL) {
//            componentWidgets.add(
//                DirectionalLightComponentWidget(
//                    uiComponentHolder, colorPickerPresenter
//                )
//            )
//        } else if (component.type == TypeComponent.Type.TERRAIN) {
//            componentWidgets.add(
//                TerrainComponentWidget(
//                    uiComponentHolder, terrainWidgetPresenter
//                )
//            )
//        } else if (component.type == TypeComponent.Type.OBJECT) {
//            componentWidgets.add(
//                ModelComponentWidget(
//                    uiComponentHolder, modelComponentPresenter
//                )
//            )
//        }


//        for (component in gameObject!!.components) {
//            // model component widget!!
//            if (component.type == Component.Type.MODEL) {
//                componentWidgets.add(
//                    ModelComponentWidget(
//                        uiWidgetsHolder.separatorStyle,
//                        component as ModelComponent,
//                        ctx,
//                        appUi,
//                        assetPickerDialog,
//                        assetManager,
//                        previewGenerator
//                    )
//                )
//                // terrainAsset component widget
//            } else if (component.type == Component.Type.TERRAIN) {
//                componentWidgets.add(
//                    TerrainComponentWidget(
//                        uiWidgetsHolder.separatorStyle,
//                        component as TerrainComponent,
//                        terrainWidgetPresenter
//                    )
//                )
//            } else if (component.type == Component.Type.LIGHT) {
//                componentWidgets.add(
//                    DirectionalLightComponentWidget(
//                        uiWidgetsHolder.separatorStyle,
//                        component as DirectionalLightComponent,
//                        colorPickerPresenter
//                    )
//                )
//            }
//        }
    }
}
