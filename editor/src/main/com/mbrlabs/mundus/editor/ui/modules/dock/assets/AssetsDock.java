package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import lombok.Getter;
import lombok.val;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class AssetsDock extends Tab/*(false, false)*/ {

    private final VisTable root = new VisTable();
    @Getter
    private final GridGroup assetsView = new GridGroup(80f, 4f);

//    private val assetItems = Array < AssetItem > ()

    @Getter
    private final PopupMenu popupMenu = new PopupMenu();
    @Getter
    private final MenuItem addAssetToScene = new MenuItem("Add Asset to Scene");
    @Getter
    private final MenuItem renameAsset = new MenuItem("Rename Asset");
    @Getter
    private final MenuItem deleteAsset = new MenuItem("Delete Asset");

    @Getter
    private AssetItem selected;

    public AssetsDock() {
        super(false, false);

        assetsView.setTouchable(Touchable.enabled);

        var contentTable = new VisTable(false);
        contentTable.add(new VisLabel("Assets")).left().padLeft(3f).row();
        contentTable.add(new Separator()).padTop(3f).expandX().fillX();
        contentTable.row();
        contentTable.add(new VisTable(false)).expandX().fillX();
        contentTable.row();
        contentTable.add(createScrollPane(assetsView, true)).expand().fill();

        var splitPane = new VisSplitPane(new VisLabel("file tree here"), contentTable, false);
        splitPane.setSplitAmount(0.2f);

        root.setBackground("window-bg");
        root.add(splitPane).expand().fill();

        // asset ops right click menu
        popupMenu.addItem(addAssetToScene);
        popupMenu.addItem(renameAsset);
        popupMenu.addItem(deleteAsset);

//        registerListeners()
    }

//    private fun registerListeners() {
//        deleteAsset.addListener(object :ClickListener() {
//            override fun clicked(event:InputEvent, x:Float, y:Float){
//                selected ?.asset ?.let {
//                    projectManager.current().assetManager.deleteAsset(it, projectManager)
//                    reloadAssets()
//                }
//            }
//        })
//        addAssetToScene.addListener(object :ClickListener() {
//            override fun clicked(event:InputEvent ?, x:Float, y:Float){
//                selected ?.asset ?.let {
//                    try {
//                        //todo
//                        Log.trace(this @AssetsDock2.javaClass.name,"Add terrain game object in root node.")
//                        val context = projectManager.current()
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
//                                sceneGraph, Shaders.modelShader, goID, name,
//                                it as ModelAsset ?
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
//                        Mundus.postEvent(AssetImportEvent(it))
//                        Mundus.postEvent(SceneGraphChangedEvent())
//                    } catch (e:Exception){
//                        e.printStackTrace()
//                    }
//                }
//            }
//        })
//    }

    public void setSelected(AssetItem assetItem) {
        selected = assetItem;
        for (var child : assetsView.getChildren()) {
            var item = (AssetItem) child;
            if (assetItem != null && assetItem == item) {
                item.background(VisUI.getSkin().getDrawable("default-select-selection"));
            } else {
                item.background(VisUI.getSkin().getDrawable("menu-bg"));
            }
        }
    }

    private VisScrollPane createScrollPane(Actor actor, boolean disableX) {
        val scrollPane = new VisScrollPane(actor);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(disableX, false);
        return scrollPane;
    }

    @Override
    public String getTabTitle() {
        return "Assets";
    }

    @Override
    public Table getContentTable() {
        return root;
    }

//    override fun
//
//    getContentTable():
//
//    Table {
//        return root
//    }
//
//    override fun
//
//    onProjectChanged(event:ProjectChangedEvent) {
//        reloadAssets()
//    }
//
//    override fun
//
//    onAssetImported(event:AssetImportEvent) {
//        reloadAssets()
//    }
//
//    override fun
//
//    onGameObjectSelected(event:GameObjectSelectedEvent) {
//        setSelected(null)
//    }

///**
// * Asset item in the grid.
// */
//    private inner
//
//    class AssetItem(val asset:Asset) :
//
//    VisTable() {
//
//        private val nameLabel:VisLabel
//
//        init {
//            setBackground("menu-bg")
//            align(Align.center)
//            nameLabel = VisLabel(asset.toString(), "tiny")
//            nameLabel.wrap = true
//            add(nameLabel).grow().top().row()
//
//            addListener(object :InputListener() {
//                override fun touchDown(event:InputEvent ?, x:Float, y:Float, pointer:Int, button:Int):Boolean {
//                    return true
//                }
//
//                override fun touchUp(event:InputEvent ?, x:Float, y:Float, pointer:Int, button:Int){
//                    if (event !!.button == Input.Buttons.RIGHT){
//                        setSelected()
//                        assetOpsMenu.showMenu(
//                                UI, Gdx.input.x.toFloat(),
//                                (Gdx.graphics.height - Gdx.input.y).toFloat()
//                        )
//                    } else if (event.button == Input.Buttons.LEFT) {
//                        setSelected()
//                    }
//                }
//
//            })
//        }
//
//        fun setSelected () {
//            this @AssetsDock.setSelected(this@AssetItem)
//                    Mundus.postEvent(AssetSelectedEvent(asset))
//        }
//    }
}

