package com.mbrlabs.mundus.editor.ui.modules.inspector.terrain.tabs

import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.ui.modules.inspector.terrain.TerrainComponentWidget

/**
 * @author JamesTKhan
 * @version May 10, 2023
 */
class TerrainRampTab(parent: TerrainComponentWidget)/* : BaseBrushTab(parent, TerrainBrush.BrushMode.RAMP) */{

    private val table = VisTable()

    init {
        table.align(Align.left)
        table.add(VisLabel("Hold shift to sample the ramp end point")).center().row()

//        table.add(terrainBrushGrid).expand().fill().row()
//        terrainBrushGrid.hideBrushes()
//        terrainBrushGrid.showCircleBrush()
    }

//    override fun getTabTitle(): String {
//        return "Ramp"
//    }
//
//    override fun getContentTable(): Table {
//        return table
//    }

}