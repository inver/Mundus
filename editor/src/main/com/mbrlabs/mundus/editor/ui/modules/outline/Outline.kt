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

//import com.mbrlabs.mundus.editor.utils.createTerrainGO
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.commons.Scene
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.events.*
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.utils.TextureUtils
import mu.KotlinLogging
import org.springframework.util.CollectionUtils

/**
 * Outline shows overview about all game objects in the scene
 *
 * @author Marcus Brummer, codenigma
 * @version 01-10-2016
 */
// TODO refactor...kind of messy spaghetti code!
@org.springframework.stereotype.Component
class Outline(
    private val ctx: EditorCtx,
    private val history: CommandHistory,
    private val eventBus: EventBus,
    private val appUi: AppUi,
    outlinePresenter: OutlinePresenter
) : VisTable(),
    ProjectChangedEvent.ProjectChangedListener,
    SceneChangedEvent.SceneChangedListener,
    SceneGraphChangedEvent.SceneGraphChangedListener,
    EntitySelectedEvent.EntitySelectedListener {

    private val log = KotlinLogging.logger {}

    private val content: VisTable
    val tree = VisTree<IdNode, Int>()
    val scrollPane: ScrollPane
    private val dragAndDrop: OutlineDragAndDrop
    val rightClickMenu = RightClickMenu()

    init {
        setBackground("window-bg")

        content = VisTable()
        content.align(Align.left or Align.top)

//        tree.debugAll()
        tree.style.plus = TextureUtils.load("ui/icons/expand.png", 20, 20)
        tree.style.minus = TextureUtils.load("ui/icons/collapse.png", 20, 20)
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
    }


    override fun onProjectChanged(event: ProjectChangedEvent) {
        // update to new sceneGraph
        log.trace("Project changed. Building scene graph.")
        buildTree(ctx.current.currentScene)
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        // update to new sceneGraph
        log.trace("Scene changed. Building scene graph.")
        buildTree(ctx.current.currentScene)
    }

    override fun onSceneGraphChanged(event: SceneGraphChangedEvent) {
        log.trace("SceneGraph changed. Building scene graph.")
        buildTree(ctx.current.currentScene)
    }

    /**
     * Building tree from game objects in sceneGraph, clearing previous
     * sceneGraph

     * @param sceneGraph
     */
    fun buildTree(scene: Scene) {
        tree.clearChildren()
        val rootNode = IdNode.RootNode()
        tree.add(rootNode)

        for (node in scene.rootNode.children) {
            addNodeToTree(rootNode.hierarchy, node)
        }
    }

    private fun addNodeToTree(treeParentNode: IdNode?, node: HierarchyNode) {
        val leaf = IdNode(node.id, node.name)
        if (treeParentNode == null) {
            tree.add(leaf)
        } else {
            treeParentNode.add(leaf)
        }
        // Always expand after adding new node
        leaf.expandTo()
        if (CollectionUtils.isEmpty(node.children)) {
            return
        }

        node.children.forEach {
            addNodeToTree(leaf, it)
        }
    }

    /**
     * Adding game object to outline

     * @param treeParentNode
     * *
     * @param gameObject
     */
    fun addGoToTree(treeParentNode: OutlineNode?, gameObject: GameObject) {
        val leaf = OutlineNode(gameObject, null)
        if (treeParentNode == null) {
//            tree.add(leaf)
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
        TODO()
//        val deleteCommand = DeleteCommand(go, tree.findNode(go))
//        history.add(deleteCommand)
//        deleteCommand.execute() // run delete
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
        val goCopy = GameObject(go, ctx.current.obtainID())
        TODO()
//        // add copy to tree
//        val n = tree.findNode(parent)
//        addGoToTree(n, goCopy)
//
//        // add copy to scene graph
//        parent.addChild(goCopy)
//
//        // recursively clone child objects
//        if (go.children != null) {
//            for (child in go.children) {
//                duplicateGO(child, goCopy)
//            }
//        }
    }

    override fun onEntitySelected(event: EntitySelectedEvent) {
        tree.selection.clear()

        if (event.entityId < 0) {
            return
        }

        val node = tree.findNode(event.entityId)
        log.trace("Select game object [{}].", node?.value)
        if (node != null) {
            tree.selection.add(node)
            node.expandTo()
        }
    }

//    override fun onGameObjectSelected(event: EntitySelectedEvent) {
//        tree.selection.clear()
//
//        if (event.gameObject == null) {
//            return
//        }
//
//        val node = tree.findNode(event.gameObject)
//        log.trace("Select game object [{}].", node?.value)
//        if (node != null) {
//            tree.selection.add(node)
//            node.expandTo()
//        }
//    }

    inner class RightClickMenu : PopupMenu() {

        val addGroup = MenuItem("Add group")
        val addCamera = MenuItem("Add camera")
        val addTerrain: MenuItem = MenuItem("Add terrain")
        private val addLight: MenuItem = MenuItem("Add light")
        val addShader = MenuItem("Add Shader")
        private val duplicate: MenuItem = MenuItem("Duplicate")
        private val rename: MenuItem = MenuItem("Rename")
        private val delete: MenuItem = MenuItem("Delete")

        private val lightsPopupMenu: PopupMenu = PopupMenu()
        val addDirectionalLight: MenuItem = MenuItem("Directional Light")

        var selectedGO = -1

        init {
            rename.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO > 0) {
                        showRenameDialog()
                    }
                }
            })

            // duplicate node
            duplicate.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO > 0 && !duplicate.isDisabled) {
                        TODO()
//                        duplicateGO(selectedGO!!, selectedGO!!.parent)
//                        eventBus.post(SceneGraphChangedEvent())
                    }
                }
            })

            // delete game object
            delete.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null) {
                        TODO()
//                        removeGo(selectedGO!!)
//                        eventBus.post(SceneGraphChangedEvent())
                    }
                }
            })

            lightsPopupMenu.addItem(addDirectionalLight)
            addLight.subMenu = lightsPopupMenu

            addItem(addGroup)
            addItem(addCamera)
            addItem(addTerrain)
            addItem(addLight)
            addItem(addShader)
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
        fun show(go: Int, x: Float, y: Float) {
            selectedGO = go
            showMenu(appUi, x, y)

            // check if game object is selected
            if (selectedGO > 0) {
                // Activate menu options for selected game objects
                rename.isDisabled = false
                delete.isDisabled = false
            } else {
                // disable MenuItems which only works with selected item
                rename.isDisabled = true
                delete.isDisabled = true
            }

            //todo
            // terrainAsset can not be duplicated
//            duplicate.isDisabled =
//                selectedGO == null || ctx.current.currentScene.world.getEntity(selectedGO).getComponent()selectedGO!!.findComponentByType(Component.Type.TERRAIN) != null
        }

        fun showRenameDialog() {
            TODO()
//            val node = tree.findNode(selectedGO!!)
//
//            val renameDialog = Dialogs.showInputDialog(appUi, "Rename", "",
//                object : InputDialogAdapter() {
//                    override fun finished(input: String?) {
//                        log.trace("Rename game object [{}] to [{}].", selectedGO, input)
//                        // update sceneGraph
//                        selectedGO!!.name = input
//                        // update Outline
//                        //goNode.name.setText(input + " [" + selectedGO.id + "]");
//                        node.label.setText(input)
//
//                        eventBus.post(SceneGraphChangedEvent())
//                    }
//                })
//            // set position of dialog to menuItem position
//            val nodePosX = node.actor.x
//            val nodePosY = node.actor.y
//            renameDialog.setPosition(nodePosX, nodePosY)
        }
    }

}
