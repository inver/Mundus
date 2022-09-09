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
package com.mbrlabs.mundus.editor.ui.modules.outline

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.*
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.DeleteCommand
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.utils.Fa
import com.mbrlabs.mundus.editor.utils.createTerrainGO
import mu.KotlinLogging

/**
 * Outline shows overview about all game objects in the scene
 *
 * @author Marcus Brummer, codenigma
 * @version 01-10-2016
 */
// TODO refactor...kind of messy spaghetti code!
@org.springframework.stereotype.Component
class Outline(
    private val toolManager: ToolManager,
    private val projectManager: ProjectManager,
    private val history: CommandHistory,
    private val eventBus: EventBus,
    private val appUi: AppUi,
    private val outlinePresenter: OutlinePresenter,
    private val bitmapFont: BitmapFont
) : VisTable(),
    ProjectChangedEvent.ProjectChangedListener,
    SceneChangedEvent.SceneChangedListener,
    SceneGraphChangedEvent.SceneGraphChangedListener,
    GameObjectSelectedEvent.GameObjectSelectedListener {

    private val log = KotlinLogging.logger {}

    private val content: VisTable
    private val tree = VisTree<OutlineNode, GameObject>()
    val scrollPane: ScrollPane
    private val dragAndDrop: OutlineDragAndDrop
    private val rightClickMenu = RightClickMenu()

    val addEmpty: MenuItem = MenuItem("Add Empty")
    val addTerrain: MenuItem = MenuItem("Add terrain")
    val duplicate: MenuItem = MenuItem("Duplicate")
    val rename: MenuItem = MenuItem("Rename")
    val delete: MenuItem = MenuItem("Delete")

    init {
        setBackground("window-bg")

//        rightClickMenu.addItem(addEmpty)
//        rightClickMenu.addItem(addTerrain)
//        rightClickMenu.addItem(duplicate)
//        rightClickMenu.addItem(rename)
//        rightClickMenu.addItem(delete)

        content = VisTable()
        content.align(Align.left or Align.top)

        tree.debugAll()
        tree.style.plus = FontAwesomeIcon(bitmapFont, Fa.PLUS_SQUARE)
        tree.style.minus = FontAwesomeIcon(bitmapFont, Fa.MINUS_SQUARE)
        tree.selection.setProgrammaticChangeEvents(false)

        dragAndDrop = OutlineDragAndDrop(tree, outlinePresenter.getDropListener(this))

        scrollPane = VisScrollPane(tree)
        scrollPane.setFlickScroll(false)
        scrollPane.setFadeScrollBars(false)
        content.add(scrollPane).fill().expand()

        add(VisLabel("Outline")).expandX().fillX().pad(3f).row()
        addSeparator().row()
        add(content).fill().expand()

        outlinePresenter.init(this)

        setupListeners()
    }


    override fun onProjectChanged(event: ProjectChangedEvent) {
        // update to new sceneGraph
        log.trace("Project changed. Building scene graph.")
        buildTree(projectManager.current.currScene.sceneGraph)
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        // update to new sceneGraph
        log.trace("Scene changed. Building scene graph.")
        buildTree(projectManager.current.currScene.sceneGraph)
    }

    override fun onSceneGraphChanged(event: SceneGraphChangedEvent) {
        log.trace("SceneGraph changed. Building scene graph.")
        buildTree(projectManager.current.currScene.sceneGraph)
    }

    private fun setupListeners() {

        tree.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (tapCount != 2)
                    return

                val go = tree.getNodeAt(y)?.value ?: return

                val pos = Vector3()
                go.transform.getTranslation(pos)

                val cam = projectManager.current.currScene.cam
                // just lerp in the direction of the object if certain distance away
                if (pos.dst(cam.position) > 100) {
                    cam.position.lerp(pos.cpy().add(0f, 40f, 0f), 0.5f)
                }

                cam.lookAt(pos)
                cam.up.set(Vector3.Y)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (Input.Buttons.LEFT != button) {
                    return true
                }
                return super.touchDown(event, x, y, pointer, button)
            }

            // right click menu listener
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (Input.Buttons.RIGHT != button) {
                    super.touchUp(event, x, y, pointer, button)
                    return
                }

                val node = tree.getNodeAt(y)
                var go: GameObject? = null
                if (node != null) {
                    go = node.value
                }
                rightClickMenu.show(go, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
            }

        })

        // select listener
        tree.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val selection = tree.selection
                if (selection != null && selection.size() > 0) {
                    val go = selection.first().value
                    projectManager.current.currScene.sceneGraph.selected = go
                    toolManager.translateTool.gameObjectSelected(go)
                    eventBus.post(GameObjectSelectedEvent(go))
                }
            }
        })

    }

    /**
     * Building tree from game objects in sceneGraph, clearing previous
     * sceneGraph

     * @param sceneGraph
     */
    fun buildTree(sceneGraph: SceneGraph) {
        tree.clearChildren()
        tree.add(OutlineNode.ROOT_NODE)

        for (go in sceneGraph.gameObjects) {
            addGoToTree(OutlineNode.ROOT_NODE, go)
        }
    }

    /**
     * Adding game object to outline

     * @param treeParentNode
     * *
     * @param gameObject
     */
    private fun addGoToTree(treeParentNode: Tree.Node<OutlineNode, GameObject, VisTable>?, gameObject: GameObject) {
        val leaf = OutlineNode(gameObject)
        if (treeParentNode == null) {
            tree.add(leaf)
        } else {
            treeParentNode.add(leaf)
        }
        // Always expand after adding new node
        leaf.expandTo()
        if (gameObject.children != null) {
            for (goChild in gameObject.children) {
                addGoToTree(leaf, goChild)
            }
        }
    }

    /**
     * Removing game object from tree and outline

     * @param go
     */
    private fun removeGo(go: GameObject) {
        // run delete command, updating sceneGraph and outline
        val deleteCommand = DeleteCommand(go, tree.findNode(go))
        history.add(deleteCommand)
        deleteCommand.execute() // run delete
    }

    /**
     * Deep copy of all game objects

     * @param go
     * *            the game object for cloning, with children
     * *
     * @param parent
     * *            game object on which clone will be added
     */
    private fun duplicateGO(go: GameObject, parent: GameObject) {
        log.trace("Duplicate [{}] with parent [{}]", go, parent)
        val goCopy = GameObject(go, projectManager.current.obtainID())

        // add copy to tree
        val n = tree.findNode(parent)
        addGoToTree(n, goCopy)

        // add copy to scene graph
        parent.addChild(goCopy)

        // recursively clone child objects
        if (go.children != null) {
            for (child in go.children) {
                duplicateGO(child, goCopy)
            }
        }
    }

    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        val node = tree.findNode(event.gameObject!!)
        log.trace("Select game object [{}].", node?.value)
        tree.selection.clear()
        tree.selection.add(node)
        node.expandTo()
    }

    private inner class RightClickMenu : PopupMenu() {

        private val addEmpty: MenuItem = MenuItem("Add Empty")
        private val addTerrain: MenuItem = MenuItem("Add terrain")
        private val duplicate: MenuItem = MenuItem("Duplicate")
        private val rename: MenuItem = MenuItem("Rename")
        private val delete: MenuItem = MenuItem("Delete")

        private var selectedGO: GameObject? = null

        init {
            // add empty
            addEmpty.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val sceneGraph = projectManager.current.currScene.sceneGraph
                    val id = projectManager.current.obtainID()
                    // the new game object
                    val go = GameObject(sceneGraph, GameObject.DEFAULT_NAME, id)
                    // update outline
                    if (selectedGO == null) {
                        // update sceneGraph
                        log.trace("Add empty game object [{}] in root node.", go)
                        sceneGraph.addGameObject(go)
                        // update outline
                        addGoToTree(null, go)
                    } else {
                        log.trace("Add empty game object [{}] child in node [{}].", go, selectedGO)
                        // update sceneGraph
                        selectedGO!!.addChild(go)
                        // update outline
                        val n = tree.findNode(selectedGO!!)
                        addGoToTree(n, go)
                    }
                    eventBus.post(SceneGraphChangedEvent())
                }
            })

            // add terrainAsset
            addTerrain.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    try {
                        log.trace("Add terrain game object in root node.")
                        val context = projectManager.current
                        val sceneGraph = context.currScene.sceneGraph
                        val goID = context.obtainID()
                        val name = "Terrain $goID"
                        // create asset
                        val asset = context.assetManager.createTerrainAsset(
                            name,
                            Terrain.DEFAULT_VERTEX_RESOLUTION, Terrain.DEFAULT_SIZE
                        )
                        asset.load()
                        asset.applyDependencies()

                        val terrainGO = createTerrainGO(
                            sceneGraph,
                            Shaders.terrainShader, goID, name, asset
                        )
                        // update sceneGraph
                        sceneGraph.addGameObject(terrainGO)
                        // update outline
                        addGoToTree(null, terrainGO)

                        context.currScene.terrains.add(asset)
                        projectManager.saveProject(context)

                        eventBus.post(AssetImportEvent(asset))
                        eventBus.post(SceneGraphChangedEvent())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })

            rename.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null) {
                        showRenameDialog()
                    }
                }
            })

            // duplicate node
            duplicate.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null && !duplicate.isDisabled) {
                        duplicateGO(selectedGO!!, selectedGO!!.parent)
                        eventBus.post(SceneGraphChangedEvent())
                    }
                }
            })

            // delete game object
            delete.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null) {
                        removeGo(selectedGO!!)
                        eventBus.post(SceneGraphChangedEvent())
                    }
                }
            })

            addItem(addEmpty)
            addItem(addTerrain)
            addItem(rename)
            addItem(duplicate)
            addItem(delete)
        }

        /**
         * Right click event opens menu and enables more options if selected
         * game object is active.
         *
         * @param go
         * @param x
         * @param y
         */
        fun show(go: GameObject?, x: Float, y: Float) {
            selectedGO = go
            showMenu(appUi, x, y)

            // check if game object is selected
            if (selectedGO != null) {
                // Activate menu options for selected game objects
                rename.isDisabled = false
                delete.isDisabled = false
            } else {
                // disable MenuItems which only works with selected item
                rename.isDisabled = true
                delete.isDisabled = true
            }

            // terrainAsset can not be duplicated
            duplicate.isDisabled =
                selectedGO == null || selectedGO!!.findComponentByType(Component.Type.TERRAIN) != null
        }

        fun showRenameDialog() {
            val node = tree.findNode(selectedGO!!)

            val renameDialog = Dialogs.showInputDialog(appUi, "Rename", "",
                object : InputDialogAdapter() {
                    override fun finished(input: String?) {
                        log.trace("Rename game object [{}] to [{}].", selectedGO, input)
                        // update sceneGraph
                        selectedGO!!.name = input
                        // update Outline
                        //goNode.name.setText(input + " [" + selectedGO.id + "]");
                        node.label.setText(input)

                        eventBus.post(SceneGraphChangedEvent())
                    }
                })
            // set position of dialog to menuItem position
            val nodePosX = node.actor.x
            val nodePosY = node.actor.y
            renameDialog.setPosition(nodePosX, nodePosY)
        }
    }

}
