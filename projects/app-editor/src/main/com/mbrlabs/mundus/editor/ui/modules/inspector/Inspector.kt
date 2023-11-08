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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.UiConstants
import com.mbrlabs.mundus.editor.ui.dsl.UiDslCreator
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiFormTable
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
@Component
class Inspector(
    private val appUi: AppUi,
    uiDslCreator: UiDslCreator
) : VisTable() {

    private val root = VisTable()
    private val scrollPane = VisScrollPane(root)

    private val goInspector: GameObjectInspector
    private val sceneInspector =
        uiDslCreator.create<UiFormTable>("com/mbrlabs/mundus/editor/ui/modules/inspector/scene/SceneWidget.groovy")
    private val textureWidget =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/texture/TextureWidget.groovy")
    private val materialWidget =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/material/MaterialWidget.groovy")
    private val dlsWidget =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/identifier/IdentifierWidget.groovy");
    private val transformWidget =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/transform/TransformWidget.groovy");
    private val terrainComponentWidgetDsl =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/terrain/TerrainWidget.groovy");
    private val modelComponentWidgetDsl =
        uiDslCreator.create<UiComponentWidget>("com/mbrlabs/mundus/editor/ui/modules/inspector/model/ModelWidget.groovy");

    init {
        goInspector = GameObjectInspector(
            uiDslCreator
        )
        init()
    }

    fun init() {
        setBackground("window-bg")
        add(VisLabel("Inspector")).expandX().fillX().row()
        addSeparator().row()
        root.debugAll()
        root.align(Align.top).padLeft(UiConstants.PAD).padRight(UiConstants.PAD).padTop(4f)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setFlickScroll(false)
        scrollPane.fadeScrollBars = false
        scrollPane.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                appUi.scrollFocus = scrollPane
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                appUi.scrollFocus = null
            }
        })

        add(scrollPane).expand().fill().top()

//        add(dlsWidget.actor).growX().pad(8f).row()
//        add(transformWidget.actor).growX().pad(8f).row()
//        add(terrainComponentWidgetDsl.actor).growX().pad(8f).row()
//        add(materialComponentWidgetDsl.actor).growX().pad(8f).row()
//        add(modelComponentWidgetDsl.actor).growX().pad(8f).row()

        root.add(textureWidget.actor).top().growX().expandX().fillX().row()
//        root.add(materialWidget.actor).top().growX().expandX().fillX().row()
        root.add(sceneInspector.actor).top().growX().expandX().fillX().row()
//        root.add(goInspector).top().growX().expandX().fillX().row()
    }
}
