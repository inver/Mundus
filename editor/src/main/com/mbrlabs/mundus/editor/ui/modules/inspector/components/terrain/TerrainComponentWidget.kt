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

import com.kotcrab.vis.ui.widget.Separator.SeparatorStyle
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.ComponentWidget

/**
 * @author Marcus Brummer
 * @version 29-01-2016
 */
class TerrainComponentWidget(
    separatorStyle: SeparatorStyle,
    terrainComponent: TerrainComponent,
    private val terrainWidgetPresenter: TerrainWidgetPresenter
) :
    ComponentWidget<TerrainComponent>(separatorStyle, "Terrain Component", terrainComponent), TabbedPaneListener {

    private val tabbedPane = TabbedPane()
    private val tabContainer = VisTable()

    private val raiseLowerTab = TerrainUpDownTab(this)
    private val flattenTab = TerrainFlattenTab(this)
    private val paintTab = TerrainPaintTab(this)
    private val genTab = TerrainGenTab(this)
    private val settingsTab = TerrainSettingsTab(this)

    init {
        tabbedPane.addListener(this)

        initRaiseLowerTab()
        initFlattenTab()
        initPaintTab()
        initGenTab()
        initSettingsTab()

        collapsibleContent.add(tabbedPane.table).growX().row()
        collapsibleContent.add(tabContainer).expand().fill().row()
        tabbedPane.switchTab(0)
    }

    private fun initSettingsTab() {
        tabbedPane.add(settingsTab)
        terrainWidgetPresenter.initSettingsTab(settingsTab)
    }

    private fun initGenTab() {
        tabbedPane.add(genTab)
        terrainWidgetPresenter.initGenTab(this, genTab)
    }

    private fun initPaintTab() {
        tabbedPane.add(paintTab)
        terrainWidgetPresenter.initBrushGrid(paintTab.grid)
        terrainWidgetPresenter.initPaintTab(this, paintTab)
    }

    private fun initFlattenTab() {
        tabbedPane.add(flattenTab)
        terrainWidgetPresenter.initBrushGrid(flattenTab.grid)
    }

    private fun initRaiseLowerTab() {
        tabbedPane.add(raiseLowerTab)
        terrainWidgetPresenter.initBrushGrid(raiseLowerTab.grid)
    }

    override fun setValues(entityId: Int) {
//        val c = go.findComponentByType(Component.Type.TERRAIN)
//        if (c != null) {
//            this.component = c as TerrainComponent
//        }
    }

    override fun switchedTab(tab: Tab) {
        tabContainer.clearChildren()
        tabContainer.add(tab.contentTable).expand().fill()
    }

    override fun removedTab(tab: Tab) {
        // no
    }

    override fun removedAllTabs() {
        // nope
    }

}
