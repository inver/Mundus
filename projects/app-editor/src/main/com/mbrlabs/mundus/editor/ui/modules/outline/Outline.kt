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
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.commons.Scene
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode
import com.mbrlabs.mundus.editor.events.*
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.utils.TextureUtils
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import java.util.*

/**
 * Outline shows overview about all game objects in the scene
 *
 * @author Marcus Brummer, codenigma
 * @version 01-10-2016
 */
// TODO refactor...kind of messy spaghetti code!
@Component
open class Outline(private val appUi: AppUi, val outlinePresenter: OutlinePresenter) : VisTable() {

    private val log = KotlinLogging.logger {}

    var selectedEntityId = -1

    val tree = VisTree<IdNode, Int>()
    val scrollPane: ScrollPane
    private val dragAndDrop: OutlineDragAndDrop

    private val rightClickMenu = PopupMenu()
    val rcmAddGroup = MenuItem("Add group")
    val rcmAddCamera = MenuItem("Add camera")
    val rcmAddTerrain = MenuItem("Add terrain")
    private val rcmAddLight = MenuItem("Add light")
    val rcmAddShader = MenuItem("Add Shader")
    val rcmDuplicate = MenuItem("Duplicate")
    val rcmRename = MenuItem("Rename")
    val rcmDelete = MenuItem("Delete")

    private val lightsPopupMenu = PopupMenu()
    val addDirectionalLight = MenuItem("Directional Light")

    init {
        setBackground("window-bg")
        add(VisLabel("Outline")).expandX().fillX().pad(3f).row()
        addSeparator().row()

//        tree.debugAll()
        tree.style.plus = TextureUtils.load("ui/icons/expand.png", 20, 20)
        tree.style.minus = TextureUtils.load("ui/icons/collapse.png", 20, 20)
        tree.selection.setProgrammaticChangeEvents(false)

        dragAndDrop = OutlineDragAndDrop(tree, outlinePresenter.getDropListener(this))

        scrollPane = VisScrollPane(tree)
        scrollPane.setFlickScroll(false)
        scrollPane.fadeScrollBars = false

        val content = VisTable()
        content.align(Align.left or Align.top)
        content.add(scrollPane).fill().expand()
        add(content).fill().expand()

        initRightClickMenu()
        initScrollPaneListener()
        initTreeClickListener()

        outlinePresenter.init(this)
    }

    private fun initScrollPaneListener() {
        scrollPane.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                appUi.setScrollFocus(scrollPane)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                appUi.setScrollFocus(null)
            }
        })
    }

    private fun initTreeClickListener() {
        tree.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (tapCount != 2) {
                    return
                }
                val clickedNode: IdNode = tree.getNodeAt(y) ?: return
                val entityId = clickedNode.value
                if (entityId < 0) {
                    return
                }
                outlinePresenter.moveCameraToSelectedEntity(entityId)
            }

            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return if (Input.Buttons.LEFT != button) {
                    true
                } else super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                if (Input.Buttons.RIGHT != button) {
                    super.touchUp(event, x, y, pointer, button)
                    return
                }
                val node = tree.getNodeAt(y)
                var id = -1
                if (node != null) {
                    id = node.value
                }
                show(id, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
            }
        })
    }

    private fun initRightClickMenu() {
        rightClickMenu.addItem(rcmAddGroup)
        rightClickMenu.addItem(rcmAddCamera)
        rightClickMenu.addItem(rcmAddTerrain)
        rightClickMenu.addItem(rcmAddLight)
        rightClickMenu.addItem(rcmAddShader)
        rightClickMenu.addItem(rcmDuplicate)
        rightClickMenu.addItem(rcmRename)
        rightClickMenu.addItem(rcmDelete)

        lightsPopupMenu.addItem(addDirectionalLight)
        rcmAddLight.subMenu = lightsPopupMenu
    }

    /**
     * Right click event opens menu and enables more options if selected
     * game object is active.
     *
     * @param entityId id of entity
     * @param x position of cursor
     * @param y position of cursor
     */
    fun show(entityId: Int, x: Float, y: Float) {
        selectedEntityId = entityId
        rightClickMenu.showMenu(appUi, x, y)

        rcmRename.isDisabled = selectedEntityId <= 0
        rcmDelete.isDisabled = selectedEntityId <= 0

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
     * Deep copy of all game objects

     * @param go
     * *            the game object for cloning, with children
     * *
     * @param parent
     * *            game object on which clone will be added
     */
//    private fun duplicateGO(go: GameObject, parent: GameObject) {
//        log.trace("Duplicate [{}] with parent [{}]", go, parent)
//        val goCopy = GameObject(go, ctx.current.obtainID())
//        TODO()
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
//    }

    fun onEntitySelected(entityId: Int) {
        tree.selection.clear()

        if (entityId < 0) {
            return
        }

        val node = tree.findNode(entityId)
        log.trace("Selected game object [{}] with id {}.", node?.value, entityId)
        if (node != null) {
            tree.selection.add(node)
            node.expandTo()
        }
    }
}
