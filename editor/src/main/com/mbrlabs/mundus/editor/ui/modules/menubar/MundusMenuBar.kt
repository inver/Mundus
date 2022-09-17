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

package com.mbrlabs.mundus.editor.ui.modules.menubar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.editor.appUimodules.menu.FileMenu
import com.mbrlabs.mundus.editor.core.registry.Registry

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
class MundusMenuBar(
    registry: Registry,
    private val menuBarController: MenuBarPresenter
) : MenuBar() {

    private val fileMenu = FileMenu(registry)
    private val editMenu = EditMenu()
    private val assetsMenu = Menu("Assets")
    private val environmentMenu = Menu("Environment")
    private val sceneMenu = SceneMenu()
    private val windowMenu = Menu("Window")

    init {
        initFileMenu()
        initEditMenu()
        initAssetsMenu()
        initEnvironmentMenu()
        initSceneMenu()
        initWindowMenu()
    }

    private fun initFileMenu() {
        addMenu(fileMenu)
        fileMenu.newProject.addListener(menuBarController.newProjectListener())
        fileMenu.importProject.addListener(menuBarController.importProjectListener())
        fileMenu.exit.addListener(menuBarController.exitListener())
        fileMenu.addRecentProjectsListener(menuBarController.recentProjectListener())
    }

    private fun initEditMenu() {
        addMenu(editMenu)
        editMenu.redo.addListener(menuBarController.redoListener())
        editMenu.undo.addListener(menuBarController.undoListener())
    }

    private fun initAssetsMenu() {
        addMenu(assetsMenu)
        assetsMenu.addItem(createMenu("Import Mesh", menuBarController.importMeshListener()))
    }

    private fun initEnvironmentMenu() {
        addMenu(environmentMenu)
        environmentMenu.addItem(createMenu("Ambient Light", menuBarController.addAmbilentLight()))
        environmentMenu.addItem(createMenu("Skybox", menuBarController.addSkyBox()))
        environmentMenu.addItem(createMenu("IBL Image", menuBarController.addIblImage()))
        environmentMenu.addItem(createMenu("Fog", menuBarController.addFog()))
    }

    private fun initSceneMenu() {
        addMenu(sceneMenu)
    }

    private fun initWindowMenu() {
        addMenu(windowMenu)
        windowMenu.addItem(createMenu("Settings", menuBarController.windowsSettingsListener()))
    }

    private fun createMenu(text: String, listener: EventListener?): MenuItem {
        val res = MenuItem(text)
        res.addListener(listener)
        return res
    }

    override fun getTable(): Table {
        val root = VisTable()
        root.setBackground("menu-bg")
        val menuTable = super.getTable()

        val icon = VisImage(Texture(Gdx.files.internal("ui/menu_icon.png")))
        root.add(icon).center().left().pad(5f)
        root.add(menuTable).expand().fill().left().center().row()
        val sep = VisTable()
        sep.background = VisUI.getSkin().getDrawable("mundus-separator-green")
        root.add(sep).expandX().fillX().height(1f).colspan(2).row()

        return root
    }

}
