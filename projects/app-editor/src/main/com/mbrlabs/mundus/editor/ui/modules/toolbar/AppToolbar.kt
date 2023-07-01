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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.core.project.ProjectContext
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.CameraChangedEvent
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.tools.*
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.modules.dialogs.ExportDialog
import com.mbrlabs.mundus.editor.ui.widgets.*
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon
import com.mbrlabs.mundus.editor.utils.Toaster
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * *
 * @version 24-11-2015
 */
@Component
class AppToolbar(
    private val ctx: EditorCtx,
    private val eventBus: EventBus,
    toolbarPresenter: AppToolbarPresenter,
    private val toolManager: ToolManager,
    private val projectManager: ProjectManager,
    private val toaster: Toaster,
    private val exportDialog: ExportDialog,
    private val appUi: AppUi,
    buttonFactory: ButtonFactory,
    private val uiStyles: UiStyles
) : Toolbar(), SceneGraphChangedEvent.SceneGraphChangedListener, ProjectChangedEvent.ProjectChangedListener {

    private val mainMenuBtn = buttonFactory.createButton(SymbolIcon.MENU)

    private val saveBtn = buttonFactory.createButton(SymbolIcon.SAVE)
    private val importBtn = buttonFactory.createButton(SymbolIcon.IMPORT)
    private val exportBtn = buttonFactory.createButton(SymbolIcon.EXPORT)

    private val selectBtn = buttonFactory.createButton(toolManager.selectionTool.icon)
    private val translateBtn = buttonFactory.createButton(toolManager.translateTool.icon)
    private val rotateBtn = buttonFactory.createButton(toolManager.rotateTool.icon)
    private val scaleBtn = buttonFactory.createButton(toolManager.scaleTool.icon)

    private val globalLocalSwitch = ToggleButton("Global space", "Local space")

    private val mainMenu = PopupMenu()
    val fileMenu = FileMenu()
    val editMenu = EditMenu()
    val assetsMenu = AssetsMenu()
    val environmentMenu = EnvironmentMenu()
    val scenesMenu = SceneMenu()
    val windowMenu = WindowMenu()

    private val importMenu = PopupMenu()
    private val importMesh = MenuItem("Import 3D model")
    private val importTexture = MenuItem("Import texture")
    private val createMaterial = MenuItem("Create material")

    private val sceneSelector = VisSelectBox<String>()
    private val cameraSelector = MundusSelectBox<HierarchyNode>()

    init {
        eventBus.register(this)

        mainMenu.addItem(fileMenu)
        mainMenu.addItem(editMenu)
        mainMenu.addItem(assetsMenu)
        mainMenu.addItem(environmentMenu)
        mainMenu.addItem(scenesMenu)
        mainMenu.addItem(windowMenu)

        importMenu.addItem(importMesh)
        importMenu.addItem(importTexture)
        importMenu.addItem(createMaterial)

        Tooltip.Builder("Save project (Ctrl+S)").target(saveBtn).build()
        Tooltip.Builder("Import model").target(importBtn).build()
        Tooltip.Builder("Export project (F1)").target(exportBtn).build()
        Tooltip.Builder(toolManager.selectionTool.name).target(selectBtn).build()
        Tooltip.Builder(toolManager.translateTool.name).target(translateBtn).build()
        Tooltip.Builder(toolManager.rotateTool.name).target(rotateBtn).build()
        Tooltip.Builder(toolManager.scaleTool.name).target(scaleBtn).build()

        sceneSelector.setItems("Main")

        cameraSelector.setValueRenderer { it.name }
        cameraSelector.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                ctx.current.selectedCamera = cameraSelector.selected.id
                eventBus.post(CameraChangedEvent(cameraSelector.selected.id))
            }
        })

        addItem(mainMenuBtn, true)
        addSeparator(true)
        addItem(saveBtn, true)
        addItem(importBtn, true)
        addItem(exportBtn, true)
        addSeparator(true)
        addItem(selectBtn, true)
        addItem(translateBtn, true)
        addItem(rotateBtn, true)
        addItem(scaleBtn, true)
        addSeparator(true)
        addItem(VisLabel(" Scene: "), true)
        addItem(sceneSelector, true)
        addSeparator(true)
        addItem(VisLabel(" Camera: "), true)
        addItem(cameraSelector, true)
        // addItem(globalLocalSwitch, true);

        toolbarPresenter.initToolbar(this)
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

        mainMenuBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                mainMenu.showMenu(appUi, mainMenuBtn)
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

    private fun setActive(btn: VisTextButton) {
        selectBtn.style = uiStyles.styleNoBg
        translateBtn.style = uiStyles.styleNoBg
        rotateBtn.style = uiStyles.styleNoBg
        scaleBtn.style = uiStyles.styleNoBg
        btn.style = uiStyles.styleActive
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

    private fun reloadCamerasList() {
        val arr = Array<HierarchyNode>()
//        arr.add(HierarchyNode(ProjectContext.MAIN_CAMERA_SELECTED, "Main", HierarchyNode.Type.CAMERA))
//        ctx.current.currentScene.rootNode.children.filter { it.type == HierarchyNode.Type.CAMERA }.forEach {
//            arr.add(it)
//        }

        cameraSelector.items = arr
    }

    override fun onSceneGraphChanged(event: SceneGraphChangedEvent) {
        reloadCamerasList()
    }

    override fun onProjectChanged(event: ProjectChangedEvent?) {
        reloadCamerasList()
    }
}
