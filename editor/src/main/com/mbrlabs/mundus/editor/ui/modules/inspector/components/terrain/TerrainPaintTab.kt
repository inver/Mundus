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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.exceptions.AssetAlreadyExistsException
import com.mbrlabs.mundus.commons.assets.meta.MetaService
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset
import com.mbrlabs.mundus.commons.terrain.SplatTexture
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.widgets.TextureGrid
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.Toaster
import java.io.IOException

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
class TerrainPaintTab(
    private val parentWidget: TerrainComponentWidget
) : Tab(false, false) {

    companion object {
        private val TAG = TerrainPaintTab::class.java.simpleName
    }

    private val root = VisTable()
    val addTextureBtn = VisTextButton("Add Texture")
    val textureGrid = TextureGrid<SplatTexture>(40, 5)
    val rightClickMenu = TextureRightClickMenu()

    val grid = TerrainBrushGrid(parentWidget, TerrainBrush.BrushMode.PAINT)

    init {
        root.align(Align.left)

        // brushes
        root.add(grid).expand().fill().padBottom(5f).row()

        // textures
        root.add(VisLabel("Textures:")).padLeft(5f).left().row()
        textureGrid.background = VisUI.getSkin().getDrawable("menu-bg")
        root.add(textureGrid).expand().fill().pad(5f).row()

        // add texture
        root.add(addTextureBtn).padRight(5f).right().row()
        setupTextureGrid()
    }

    private fun setupTextureGrid() {


        textureGrid.setListener { texture, leftClick ->
            val tex = texture as SplatTexture
            if (leftClick) {
                TerrainBrush.setPaintChannel(tex.channel)
            } else {
                rightClickMenu.setChannel(tex.channel)
                rightClickMenu.show()
            }
        }

        setTexturesInUiGrid()
    }

    fun setTexturesInUiGrid() {
        textureGrid.removeTextures()
        val terrainTexture = parentWidget.component.terrain.terrain.terrainTexture
        if (terrainTexture.getTexture(SplatTexture.Channel.BASE) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.BASE))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.R) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.R))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.G) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.G))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.B) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.B))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.A) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.A))
        }
    }

    override fun getTabTitle(): String {
        return "Paint"
    }

    override fun getContentTable(): Table {
        return root
    }

    /**

     */
    inner class TextureRightClickMenu : PopupMenu() {

        private val removeTexture = MenuItem("Remove texture")
        private val changeTexture = MenuItem("Change texture")

        private var channel: SplatTexture.Channel? = null

        init {
            addItem(removeTexture)
            addItem(changeTexture)
        }

        fun addRemoveListener(listener: ChangeTextureListener) {
            removeTexture.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (channel != null) {
                        listener.change(channel!!)
                    }
                }
            })
        }

        fun addChangeListener(listener: ChangeTextureListener) {
            changeTexture.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (channel != null) {
                        listener.change(channel!!)
                    }
                }
            })
        }

        fun setChannel(channel: SplatTexture.Channel) {
            this.channel = channel
        }

        fun show() {
            showMenu(UI, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
        }

    }

    interface ChangeTextureListener {
        fun change(channel: SplatTexture.Channel);
    }
}
