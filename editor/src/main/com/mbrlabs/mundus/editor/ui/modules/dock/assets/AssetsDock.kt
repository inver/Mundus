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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
class AssetsDock : Tab(false, false),
    ProjectChangedEvent.ProjectChangedListener,
    AssetImportEvent.AssetImportListener,
    GameObjectSelectedEvent.GameObjectSelectedListener {

    private val root = VisTable()


    //    private val assetItems = Array<AssetItem>()
//
//    private val assetOpsMenu = PopupMenu()
//    private val renameAsset = MenuItem("Rename Asset")
//    private val deleteAsset = MenuItem("Delete Asset")
//    private val addAssetToScene = MenuItem("Add Asset to Scene")
//
//    private var currentSelection: AssetItem? = null
    private var assetsTabs: TabbedPane

    val projectAssets = AssetLibraryViewer("Project assets")
    val appAssets = AssetLibraryViewer("Asset library")

    init {
        val style = TabbedPane.TabbedPaneStyle(VisUI.getSkin().get(TabbedPane.TabbedPaneStyle::class.java))
        style.buttonStyle = VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get("toggle", VisTextButton.VisTextButtonStyle::class.java)
        )

        assetsTabs = TabbedPane(style)
        assetsTabs.addListener(object : TabbedPaneListener {
            override fun switchedTab(tab: Tab?) {
            }

            override fun removedTab(tab: Tab?) {

            }

            override fun removedAllTabs() {

            }

        })
        assetsTabs.add(projectAssets.tab)
        assetsTabs.add(appAssets.tab)
        assetsTabs.switchTab(projectAssets.tab)

        val splitPane = VisSplitPane(VisLabel("file tree here"), assetsTabs.table, false)
        splitPane.setSplitAmount(0.2f)

        root.setBackground("window-bg")
        root.add(splitPane).expand().fill()

        initUi()
    }

    fun initUi() {
//        val style = TabbedPane.TabbedPaneStyle(VisUI.getSkin().get(TabbedPane.TabbedPaneStyle::class.java))
//        style.buttonStyle = VisTextButton.VisTextButtonStyle(
//            VisUI.getSkin().get("toggle", VisTextButton.VisTextButtonStyle::class.java)
//        )
//
//        assetsTabs = TabbedPane(style)
//        assetsTabs.addListener(this)
//
//        tabbedPane.add(assetsDock)
//        tabbedPane.add(logBar)
//        add(tabbedPane.table).expandX().fillX().left().bottom().height(30f).row()
//
//        // Keeping asset tab the default active tab
//        tabbedPane.switchTab(assetsDock)
//
//
//        filesView.touchable = Touchable.enabled
//
//        val contentTable = VisTable(false)
//        contentTable.add(VisLabel("Assets")).left().padLeft(3f).row()
//        contentTable.add(Separator()).padTop(3f).expandX().fillX()
//        contentTable.row()
//        contentTable.add(filesViewContextContainer).expandX().fillX()
//        contentTable.row()
//        contentTable.add(createScrollPane(filesView, true)).expand().fill()
//
//        val splitPane = VisSplitPane(VisLabel("file tree here"), contentTable, false)
//        splitPane.setSplitAmount(0.2f)

//        root.setBackground("window-bg")
//        root.add(splitPane).expand().fill()

//        // asset ops right click menu
//        assetOpsMenu.addItem(renameAsset)
//        assetOpsMenu.addItem(deleteAsset)
//        assetOpsMenu.addItem(addAssetToScene)
//
//        registerListeners()
    }

//    private fun registerListeners() {
//        deleteAsset.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent, x: Float, y: Float) {
//                currentSelection?.asset?.let {
//                    projectManager.current.assetManager.deleteAsset(it, projectManager)
//                    reloadAssets()
//                }
//            }
//        })
//        addAssetToScene.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                currentSelection?.asset?.let {
//                    try {
//                        //todo
//                        Log.trace(this@AssetsDock.javaClass.name, "Add terrain game object in root node.")
//                        val context = projectManager.current
//                        val sceneGraph = context.currScene.sceneGraph
//                        val goID = context.obtainID()
//                        val name = "${it.meta.type} $goID"
//                        // create asset
////                        val asset = context.assetManager.createModelAsset(it.file)
//
////                        asset.load()
////                        asset.applyDependencies()
//
//                        val modelGO = GameObjectUtils.createModelGO(
//                            sceneGraph, Shaders.modelShader, goID, name,
//                            it as ModelAsset?
//                        )
////                        val terrainGO = createTerrainGO(
////                            sceneGraph,
////                            Shaders.terrainShader, goID, name, asset
////                        )
//                        // update sceneGraph
//                        sceneGraph.addGameObject(modelGO)
//                        // update outline
//                        //todo
////                        addGoToTree(null, terrainGO)
//
////                        context.currScene..add(asset)
//                        projectManager.saveProject(context)
//                        eventBus.post(AssetImportEvent(it))
//                        eventBus.post(SceneGraphChangedEvent())
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        })
//    }

//    private fun setSelected(assetItem: AssetItem?) {
//        currentSelection = assetItem
//        for (item in assetItems) {
//            if (currentSelection != null && currentSelection == item) {
//                item.background(VisUI.getSkin().getDrawable("default-select-selection"))
//            } else {
//                item.background(VisUI.getSkin().getDrawable("menu-bg"))
//            }
//        }
//    }
//
//    private fun reloadAssets() {
//        filesView.clearChildren()
//        val projectContext = projectManager.current
//        for (asset in projectContext.assetManager.assets) {
//            val assetItem = AssetItem(asset)
//            filesView.addActor(assetItem)
//            assetItems.add(assetItem)
//        }
//    }

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
//        reloadAssets()
    }

    override fun onAssetImported(event: AssetImportEvent) {
//        reloadAssets()
    }

    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
//        setSelected(null)
    }

    /**
     * Asset item in the grid.
     */

}
