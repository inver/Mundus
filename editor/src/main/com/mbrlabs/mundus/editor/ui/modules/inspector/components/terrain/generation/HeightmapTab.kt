package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.generation

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.TerrainHeightCommand
import com.mbrlabs.mundus.editor.terrain.Terraformer
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField
import com.mbrlabs.mundus.editor.utils.isImage

class HeightmapTab(
    private val terrainAsset: TerrainAsset,
    private val appUi: AppUi,
    private val fileChooser: FileChooser,
    private val projectManager: ProjectManager,
    private val history: CommandHistory
) : Tab(false, false) {

    private val root = VisTable()

    private val hmInput = FileChooserField(appUi, fileChooser)
    private val loadHeightMapBtn = VisTextButton("Load heightmap")

    init {
        root.align(Align.left)

        root.add(hmInput).pad(5f).left().expandX().fillX().row()
        root.add(loadHeightMapBtn).pad(5f).right().row()

        setupListeners()
    }

    override fun getTabTitle(): String = "Heightmap"

    override fun getContentTable(): Table = root

    private fun setupListeners() {
        loadHeightMapBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val hm = hmInput.file
                if (hm != null && hm.exists() && isImage(hm)) {
                    loadHeightMap(hm)
                    projectManager.current.assetManager.dirty(terrainAsset)
                } else {
                    Dialogs.showErrorDialog(UI, "Please select a heightmap image")
                }
            }
        })
    }

    private fun loadHeightMap(heightMap: FileHandle) {
        val terrain = terrainAsset.terrain
        val command = TerrainHeightCommand(terrain)
        command.setHeightDataBefore(terrain.heightData)

        val originalMap = Pixmap(heightMap)

        // scale pixmap if it doesn't fit the terrainAsset
        if (originalMap.width != terrain.vertexResolution || originalMap.height != terrain.vertexResolution) {
            val scaledPixmap = Pixmap(
                terrain.vertexResolution, terrain.vertexResolution,
                originalMap.format
            )
            scaledPixmap.drawPixmap(
                originalMap, 0, 0, originalMap.width, originalMap.height, 0, 0,
                scaledPixmap.width, scaledPixmap.height
            )

            originalMap.dispose()
            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(scaledPixmap).terraform()
            scaledPixmap.dispose()
        } else {
            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(originalMap).terraform()
            originalMap.dispose()
        }

        command.setHeightDataAfter(terrain.heightData)
        history.add(command)
    }
}
