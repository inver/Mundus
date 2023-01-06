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

package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent.SceneGraphChangedListener
import com.mbrlabs.mundus.editor.tools.*
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.modules.dialogs.ExportDialog
import com.mbrlabs.mundus.editor.ui.widgets.FaTextButton
import com.mbrlabs.mundus.editor.ui.widgets.ToggleButton
import com.mbrlabs.mundus.editor.ui.widgets.Toolbar
import com.mbrlabs.mundus.editor.utils.Fa
import com.mbrlabs.mundus.editor.utils.Toaster
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * *
 * @version 24-11-2015
 */
@Component
class MundusToolbar(
    private val ctx: EditorCtx,
    private val eventBus: EventBus,
    toolbarPresenter: ToolbarPresenter,
    private val toolManager: ToolManager,
    private val projectManager: ProjectManager,
    private val toaster: Toaster,
    private val exportDialog: ExportDialog,
    private val appUi: AppUi
) : Toolbar(), SceneGraphChangedEvent.SceneGraphChangedListener {
    private val saveBtn = FaTextButton(Fa.SAVE)
    private val importBtn = FaTextButton(Fa.DOWNLOAD)
    private val exportBtn = FaTextButton(Fa.GIFT)

    private val selectBtn: FaTextButton
    private val translateBtn: FaTextButton
    private val rotateBtn: FaTextButton
    private val scaleBtn: FaTextButton
    private val globalLocalSwitch = ToggleButton("Global space", "Local space")

    private val importMenu = PopupMenu()
    private val importMesh = MenuItem("Import 3D model")
    private val importTexture = MenuItem("Import texture")
    private val createMaterial = MenuItem("Create material")

    private val sceneSelector = VisSelectBox<String>();
    private val cameraSelector = VisSelectBox<String>();

    init {
        eventBus.register(this)

        importMenu.addItem(importMesh)
        importMenu.addItem(importTexture)
        importMenu.addItem(createMaterial)

        saveBtn.padRight(7f).padLeft(7f)
        Tooltip.Builder("Save project (Ctrl+S)").target(saveBtn).build()

        importBtn.padRight(7f).padLeft(7f)
        Tooltip.Builder("Import model").target(importBtn).build()

        exportBtn.padRight(12f).padLeft(7f)
        Tooltip.Builder("Export project (F1)").target(exportBtn).build()

        selectBtn = FaTextButton(toolManager.selectionTool.iconFont)
        selectBtn.padRight(7f).padLeft(12f)
        Tooltip.Builder(toolManager.selectionTool.name).target(selectBtn).build()

        translateBtn = FaTextButton(toolManager.translateTool.iconFont)
        translateBtn.padRight(7f).padLeft(7f)
        Tooltip.Builder(toolManager.translateTool.name).target(translateBtn).build()

        rotateBtn = FaTextButton(toolManager.rotateTool.iconFont)
        rotateBtn.padRight(7f).padLeft(7f)
        Tooltip.Builder(toolManager.rotateTool.name).target(rotateBtn).build()

        scaleBtn = FaTextButton(toolManager.scaleTool.iconFont)
        scaleBtn.padRight(7f).padLeft(7f)
        Tooltip.Builder(toolManager.scaleTool.name).target(scaleBtn).build()

        sceneSelector.setItems("Main");
        cameraSelector.setItems("Main", "Pilot")
//        cameraSelector.addListener(object )

        addItem(saveBtn, true)
        addItem(importBtn, true)
        addItem(exportBtn, true)
        addSeperator(true)
        addItem(selectBtn, true)
        addItem(translateBtn, true)
        addItem(rotateBtn, true)
        addItem(scaleBtn, true)
        addSeperator(true)
        addItem(VisLabel(" Scene: "), true)
        addItem(sceneSelector, true)
        addSeperator(true)
        addItem(VisLabel(" Camera: "), true)
        addItem(cameraSelector, true)
        // addItem(globalLocalSwitch, true);

        setActive(translateBtn)

        // save btn
        saveBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                projectManager.saveCurrentProject()
                toaster.success("Project saved")
            }
        })

        // export btn
        exportBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                exportDialog.export()
            }
        })

        // import btn
        importBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                importMenu.showMenu(appUi, importBtn)
            }
        })


        importMesh.addListener(toolbarPresenter.importMeshListener())
        importTexture.addListener(toolbarPresenter.importTextureListener())
        createMaterial.addListener(toolbarPresenter.createMaterialListener())

        // select tool
        selectBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                toolManager.activateTool(toolManager.selectionTool)
                setActive(selectBtn)
            }
        })

        // translate tool
        translateBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                toolManager.activateTool(toolManager.translateTool)
                setActive(translateBtn)
            }
        })

        // rotate tool
        rotateBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                toolManager.activateTool(toolManager.rotateTool)
                setActive(rotateBtn)
            }
        })

        // scale tool
        scaleBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                toolManager.activateTool(toolManager.scaleTool)
                setActive(scaleBtn)
            }
        })

        // global / local space switching
        globalLocalSwitch.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                toolManager.translateTool.setGlobalSpace(globalLocalSwitch.isOn)
            }
        })

    }

    private fun setActive(btn: FaTextButton) {
        selectBtn.style = FaTextButton.styleNoBg
        translateBtn.style = FaTextButton.styleNoBg
        rotateBtn.style = FaTextButton.styleNoBg
        scaleBtn.style = FaTextButton.styleNoBg
        btn.style = FaTextButton.styleActive
    }

    /**
     * Refresh the active button in UI with the currently active tool.
     */
    fun updateActiveToolButton() {
        when (toolManager.activeTool.name) {
            SelectionTool.NAME -> setActive(selectBtn)
            TranslateTool.NAME -> setActive(translateBtn)
            RotateTool.NAME -> setActive(rotateBtn)
            ScaleTool.NAME -> setActive(scaleBtn)
        }
    }

    override fun onSceneGraphChanged(event: SceneGraphChangedEvent) {
        val cameras = ArrayList<com.mbrlabs.mundus.commons.scene3d.components.Component>()
        ctx.current.currentScene.sceneGraph.gameObjects.forEach {
            it.findComponentsByType(cameras, com.mbrlabs.mundus.commons.scene3d.components.Component.Type.CAMERA, true)
        }

        val arr = Array<String>()
        arr.add("Main")
        cameras.forEach() {
            arr.add(it.gameObject.parent.name)
        }
        cameraSelector.items = arr
    }
}
