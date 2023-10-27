package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.tabs.generation

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.history.Command
import com.mbrlabs.mundus.editor.ui.widgets.chooser.file.FileChooserField

class HeightmapTab : Tab(false, false) {

    private val root = VisTable()

    val fileField = FileChooserField()
    val loadHeightMapBtn = VisTextButton("Load heightmap")

    var selectedFile: FileHandle? = null

    init {
        root.align(Align.left)
        root.add(fileField).pad(5f).left().expandX().fillX().row()
        root.add(loadHeightMapBtn).pad(5f).right().row()

        fileField.setCallback {
            selectedFile = it
        }
    }

    override fun getTabTitle(): String = "Heightmap"

    override fun getContentTable(): Table = root

    //todo move to presenter
    fun loadHeightMap(heightMap: FileHandle): Command? {
//        val terrain = terrainAsset.terrain
//        val command = TerrainHeightCommand(terrain)
//        command.setHeightDataBefore(terrain.heightData)
//
//        val originalMap = Pixmap(heightMap)
//
//        // scale pixmap if it doesn't fit the terrainAsset
//        if (originalMap.width != terrain.vertexResolution || originalMap.height != terrain.vertexResolution) {
//            val scaledPixmap = Pixmap(
//                terrain.vertexResolution, terrain.vertexResolution,
//                originalMap.format
//            )
//            scaledPixmap.drawPixmap(
//                originalMap, 0, 0, originalMap.width, originalMap.height, 0, 0,
//                scaledPixmap.width, scaledPixmap.height
//            )
//
//            originalMap.dispose()
//            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(scaledPixmap).terraform()
//            scaledPixmap.dispose()
//        } else {
//            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(originalMap).terraform()
//            originalMap.dispose()
//        }
//
//        command.setHeightDataAfter(terrain.heightData)
//        return command
        return null
    }
}
