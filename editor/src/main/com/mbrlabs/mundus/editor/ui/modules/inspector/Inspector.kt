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
import com.mbrlabs.mundus.editor.config.UiWidgetsHolder
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.PreviewGenerator
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.TerrainWidgetPresenter
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerPresenter
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
@Component
class Inspector(
    eventBus: EventBus,
    private val ctx: EditorCtx,
    private val assetManager: EditorAssetManager,
    private val appUi: AppUi,
    private val uiWidgetsHolder: UiWidgetsHolder,
    private val assetPickerDialog: AssetPickerDialog,
    private val toolManager: ToolManager,
    private val terrainWidgetPresenter: TerrainWidgetPresenter,
    private val history: CommandHistory,
    private val previewGenerator: PreviewGenerator,
    private val colorPickerPresenter: ColorPickerPresenter
) : VisTable(),
    GameObjectModifiedEvent.GameObjectModifiedListener,
    AssetSelectedEvent.AssetSelectedListener,
    EntitySelectedEvent.EntitySelectedListener {

    companion object {
        @JvmStatic
        private val log = getLogger(Inspector::class.java)
    }

    enum class InspectorMode {
        GAME_OBJECT, ASSET, EMPTY
    }

    private var mode = InspectorMode.EMPTY
    private val root = VisTable()
    private val scrollPane = VisScrollPane(root)

    private val goInspector: GameObjectInspector
    private val assetInspector: AssetInspector

    init {
        eventBus.register(this)

        goInspector = GameObjectInspector(
            ctx,
            appUi,
            uiWidgetsHolder,
            assetPickerDialog,
            assetManager,
            history,
            terrainWidgetPresenter,
            previewGenerator,
            colorPickerPresenter
        )
        assetInspector = AssetInspector(
            uiWidgetsHolder.separatorStyle,
            ctx,
            appUi,
            assetManager,
            assetPickerDialog,
            toolManager,
            previewGenerator,
            colorPickerPresenter
        )

        init()
    }

    fun init() {
        setBackground("window-bg")
        add(VisLabel("Inspector")).expandX().fillX().pad(3f).row()
        addSeparator().row()
        root.align(Align.top)
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
    }

    override fun onEntitySelected(event: EntitySelectedEvent) {
        if (mode != InspectorMode.GAME_OBJECT) {
            mode = InspectorMode.GAME_OBJECT
            root.clear()
            root.add(goInspector).grow().row()
        }
        goInspector.setEntity(event.entityId)
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
