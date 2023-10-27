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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.input.InputService
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.PreviewGenerator
import com.mbrlabs.mundus.editor.ui.UiComponentHolder
import com.mbrlabs.mundus.editor.ui.UiConstants
import com.mbrlabs.mundus.editor.ui.dsl.UiDslCreator
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.model.ModelComponentPresenter
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.TerrainWidgetPresenter
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiFormTable
import com.mbrlabs.mundus.editor.ui.widgets.chooser.color.ColorChooserPresenter
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
@Component
class Inspector(
    private val ctx: EditorCtx,
    private val appUi: AppUi,
    eventBus: EventBus,
    assetManager: EditorAssetManager,
    uiComponentHolder: UiComponentHolder,
    assetPickerDialog: AssetPickerDialog,
    inputService: InputService,
    toolManager: ToolManager,
    terrainWidgetPresenter: TerrainWidgetPresenter,
    history: CommandHistory,
    previewGenerator: PreviewGenerator,
    colorPickerPresenter: ColorChooserPresenter,
    modelComponentPresenter: ModelComponentPresenter,
    uiDslCreator: UiDslCreator,
    applicationContext: ApplicationContext
) : VisTable(),
    GameObjectModifiedEvent.GameObjectModifiedListener,
    AssetSelectedEvent.AssetSelectedListener,
    EntitySelectedEvent.EntitySelectedListener {

    companion object {
        @JvmStatic
        private val log = getLogger(Inspector::class.java)
    }

    enum class InspectorMode {
        GAME_OBJECT, ASSET, SCENE, EMPTY
    }

    private var mode = InspectorMode.EMPTY
    private val root = VisTable()
    private val scrollPane = VisScrollPane(root)

    private val goInspector: GameObjectInspector
    private val assetInspector: AssetInspector
    private val sceneInspector =
        uiDslCreator.create<UiFormTable>("com/mbrlabs/mundus/editor/ui/modules/inspector/scene/SceneWidget.groovy")
//    private val cameraInspector = CameraInspector(previewGenerator, appUi)

    init {
        eventBus.register(this)

        goInspector = GameObjectInspector(
            ctx,
            uiComponentHolder,
            terrainWidgetPresenter,
            uiDslCreator,
            applicationContext
        )
        assetInspector = AssetInspector(
            ctx,
            appUi,
            assetManager,
            assetPickerDialog,
            inputService,
            toolManager,
            previewGenerator,
            colorPickerPresenter,
            uiComponentHolder,
            applicationContext
        )

        init()
    }

    fun init() {
        setBackground("window-bg")
        add(VisLabel("Inspector")).expandX().fillX().pad(3f).row()
        addSeparator().row()
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

        add<ScrollPane>(scrollPane).expand().fill().top()
        root.add(sceneInspector.actor).growX().row()
        root.add(goInspector).growX().row()
    }

    override fun onEntitySelected(event: EntitySelectedEvent) {
//        if (event.entityId == RootNode.ROOT_NODE_ID && mode != InspectorMode.SCENE) {
//            mode = InspectorMode.SCENE
//            root.clear()
//            root.add(sceneInspector.actor).growX().row()
//            goInspector.setEntity(event.entityId)
//            return
//        }
//
//        val type = ctx.getComponentByEntityId(event.entityId, TypeComponent::class.java)?.type
//
//        if (type != TypeComponent.Type.CAMERA && type != TypeComponent.Type.GROUP
//            && type != TypeComponent.Type.HANDLE && mode != InspectorMode.GAME_OBJECT
//        ) {
//            mode = InspectorMode.GAME_OBJECT
//            root.clear()
//            root.add(goInspector).grow().row()
//            goInspector.setEntity(event.entityId)
//            return
//        }
    }

    override fun onGameObjectModified(event: GameObjectModifiedEvent) {
        goInspector.updateGameObject()
    }

    override fun onAssetSelected(event: AssetSelectedEvent) {
        log.debug(event.asset.toString())
        if (mode != InspectorMode.ASSET) {
            mode = InspectorMode.ASSET
            root.clear()
            root.add(assetInspector).grow().row()
        }
        assetInspector.asset = event.asset
    }

}
