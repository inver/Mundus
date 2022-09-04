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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.GlobalBrushSettingsChangedEvent
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.widgets.FaTextButton
import com.mbrlabs.mundus.editor.ui.widgets.ImprovedSlider

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
class TerrainBrushGrid(private val parent: TerrainComponentWidget) : VisTable(),
    GlobalBrushSettingsChangedEvent.GlobalBrushSettingsChangedListener {

    var brushMode: TerrainBrush.BrushMode? = null

    val grid = GridGroup(40f, 0f)
    private val strengthSlider = ImprovedSlider(0f, 1f, 0.1f)

    init {
        Mundus.registerEventListener(this)
        align(Align.left)
        add(VisLabel("Brushes:")).padBottom(10f).padLeft(5f).left().row()

        val brushGridContainerTable = VisTable()
        brushGridContainerTable.setBackground("menu-bg")
        brushGridContainerTable.add(grid).expand().fill().row()

        // brush settings
        val settingsTable = VisTable()
        settingsTable.add(VisLabel("Strength")).left().row()
        strengthSlider.value = TerrainBrush.getStrength()
        settingsTable.add(strengthSlider).expandX().fillX().row()
        strengthSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                TerrainBrush.setStrength(strengthSlider.value)
            }
        })

        add(brushGridContainerTable).expand().fill().padLeft(5f).padRight(5f).row()
        add(settingsTable).expand().fill().padLeft(5f).padRight(5f).padTop(5f).row()
    }

    constructor(parent: TerrainComponentWidget, mode: TerrainBrush.BrushMode) : this(parent) {
        this.brushMode = mode
    }

    fun activateBrush(brush: TerrainBrush) {
        brush.mode = brushMode
        brush.terrainAsset = parent.component.terrain
    }

    override fun onSettingsChanged(event: GlobalBrushSettingsChangedEvent) {
        strengthSlider.value = TerrainBrush.getStrength()
    }

    fun addBrush(brush: TerrainBrush): BrushItem {
        val res = BrushItem(brush)
        grid.addActor(res)
        return res
    }

    /**
     */
    inner class BrushItem(brush: TerrainBrush) : VisTable() {
        init {
            add(FaTextButton(brush.iconFont))
        }
    }

}
