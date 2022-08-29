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

package com.mbrlabs.mundus.editor.ui.modules.dock.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.ModelAsset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.*
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.GameObjectUtils
import com.mbrlabs.mundus.editor.utils.Log

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
class AssetsDock : Tab(false, false),
    ProjectChangedEvent.ProjectChangedListener,
    AssetImportEvent.AssetImportListener,
    GameObjectSelectedEvent.GameObjectSelectedListener {

    private val root = VisTable()
    private val filesViewContextContainer = VisTable(false)
    private val filesView = GridGroup(80f, 4f)

    private val assetItems = Array<AssetItem>()

    private val assetOpsMenu = PopupMenu()
    private val renameAsset = MenuItem("Rename Asset")
    private val deleteAsset = MenuItem("Delete Asset")
    private val addAssetToScene = MenuItem("Add Asset to Scene")

    private var currentSelection: AssetItem? = null
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)
        initUi()
    }

    fun initUi() {
        filesView.touchable = Touchable.enabled

        val contentTable = VisTable(false)
        contentTable.add(VisLabel("Assets")).left().padLeft(3f).row()
        contentTable.add(Separator()).padTop(3f).expandX().fillX()
        contentTable.row()
        contentTable.add(filesViewContextContainer).expandX().fillX()
        contentTable.row()
        contentTable.add(createScrollPane(filesView, true)).expand().fill()

        val splitPane = VisSplitPane(VisLabel("file tree here"), contentTable, false)
        splitPane.setSplitAmount(0.2f)

        root.setBackground("window-bg")
        root.add(splitPane).expand().fill()

        // asset ops right click menu
        assetOpsMenu.addItem(renameAsset)
        assetOpsMenu.addItem(deleteAsset)
        assetOpsMenu.addItem(addAssetToScene)

        registerListeners()
    }

    private fun registerListeners() {
        deleteAsset.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                currentSelection?.asset?.let {
                    projectManager.current().assetManager.deleteAsset(it, projectManager)
                    reloadAssets()
                }
            }
        })
        addAssetToScene.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                currentSelection?.asset?.let {
                    try {
                        //todo
                        Log.trace(this@AssetsDock.javaClass.name, "Add terrain game object in root node.")
                        val context = projectManager.current()
                        val sceneGraph = context.currScene.sceneGraph
                        val goID = context.obtainID()
                        val name = "${it.meta.type} $goID"
                        // create asset
//                        val asset = context.assetManager.createModelAsset(it.file)

//                        asset.load()
//                        asset.applyDependencies()

                        val modelGO = GameObjectUtils.createModelGO(
                            sceneGraph, Shaders.modelShader, goID, name,
                            it as ModelAsset?
                        )
//                        val terrainGO = createTerrainGO(
//                            sceneGraph,
//                            Shaders.terrainShader, goID, name, asset
//                        )
                        // update sceneGraph
                        sceneGraph.addGameObject(modelGO)
                        // update outline
                        //todo
//                        addGoToTree(null, terrainGO)

//                        context.currScene..add(asset)
                        projectManager.saveProject(context)
                        Mundus.postEvent(AssetImportEvent(it))
                        Mundus.postEvent(SceneGraphChangedEvent())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun setSelected(assetItem: AssetItem?) {
        currentSelection = assetItem
        for (item in assetItems) {
            if (currentSelection != null && currentSelection == item) {
                item.background(VisUI.getSkin().getDrawable("default-select-selection"))
            } else {
                item.background(VisUI.getSkin().getDrawable("menu-bg"))
            }
        }
    }

    private fun reloadAssets() {
        filesView.clearChildren()
        val projectContext = projectManager.current()
        for (asset in projectContext.assetManager.assets) {
            val assetItem = AssetItem(asset)
            filesView.addActor(assetItem)
            assetItems.add(assetItem)
        }
    }

    private fun createScrollPane(actor: Actor, disableX: Boolean): VisScrollPane {
        val scrollPane = VisScrollPane(actor)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setScrollingDisabled(disableX, false)
        return scrollPane
    }

    override fun getTabTitle(): String {
        return "Assets"
    }

    override fun getContentTable(): Table {
        return root
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        reloadAssets()
    }

    override fun onAssetImported(event: AssetImportEvent) {
        reloadAssets()
    }

    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        setSelected(null)
    }

    /**
     * Asset item in the grid.
     */
    private inner class AssetItem(val asset: Asset) : VisTable() {

        private val nameLabel: VisLabel

        init {
            setBackground("menu-bg")
            align(Align.center)
            nameLabel = VisLabel(asset.toString(), "tiny")
            nameLabel.wrap = true
            add(nameLabel).grow().top().row()

            addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    return true
                }

                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    if (event!!.button == Input.Buttons.RIGHT) {
                        setSelected()
                        assetOpsMenu.showMenu(
                            UI, Gdx.input.x.toFloat(),
                            (Gdx.graphics.height - Gdx.input.y).toFloat()
                        )
                    } else if (event.button == Input.Buttons.LEFT) {
                        setSelected()
                    }
                }

            })
        }

        fun setSelected() {
            this@AssetsDock.setSelected(this@AssetItem)
            Mundus.postEvent(AssetSelectedEvent(asset))
        }
    }
}
