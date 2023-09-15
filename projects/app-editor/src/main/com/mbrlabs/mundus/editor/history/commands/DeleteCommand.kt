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
package com.mbrlabs.mundus.editor.history.commands

import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.history.Command
import com.mbrlabs.mundus.editor.ui.modules.outline.IdNode
import org.slf4j.LoggerFactory

/**
 * Delete command for game objects Deletion will update sceneGraph and outline
 *
 * @author codenigma
 * @version 28-09-2016
 */
class DeleteCommand(var entityId: Int, private var node: IdNode) : Command {

    companion object {
        private val log = LoggerFactory.getLogger(DeleteCommand::class.java)
    }

    //    private var parentGO: GameObject? = null
    private var parentNode: Tree.Node<IdNode, Int, VisTable>? = null
    private var tree: Tree<IdNode, Int>? = null

    init {
//        this.parentGO = entityId!!.parent
        this.parentNode = node.parent
        this.tree = node.tree
    }

    override fun execute() {
        log.trace("Remove game object [{}]", entityId)

        // remove go from sceneGraph
//        entityId!!.remove()
        // remove from outline tree
//        tree!!.remove(node)
    }

    override fun undo() {
        TODO()
//        log.trace("Undo remove of game object [{}]", entityId)
        // add to sceneGraph
//        parentGO!!.addChild(entityId)
        // add to outline
//        if (parentNode == null)
//            tree!!.add(node)
//        else
//            parentNode!!.add(node)
//        node.expandTo()
    }

}
