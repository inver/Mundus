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

package com.mbrlabs.mundus.editor.appUimodules.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.mbrlabs.mundus.commons.core.registry.ProjectRef
import com.mbrlabs.mundus.commons.core.registry.Registry

/**
 * @author Marcus Brummer
 * *
 * @version 22-11-2015
 */
class FileMenu(
    private val registry: Registry,
) : Menu("File") {

    val newProject = MenuItem("New Project")
    val importProject = MenuItem("Import Project")
    val exit = MenuItem("Exit")

    private val recentProjects = MenuItem("Recent Projects")
    private val recentProjectsPopup = PopupMenu()
    private val saveProject = MenuItem("Save Project")

    init {
        newProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.N)
        importProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O)
        saveProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S)

        recentProjects.subMenu = recentProjectsPopup

        addItem(newProject)
        addItem(importProject)
        addItem(saveProject)
        addItem(recentProjects)
        addSeparator()
        addItem(exit)
    }

    fun addRecentProjectsListener(listener: RecentProjectListener) {
        for (proj in registry.projects) {
            val menu = MenuItem(proj.name + " - [" + proj.path + "]")
            menu.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    listener.clicked(proj);
                }
            })
            recentProjectsPopup.addItem(menu)
        }
    }

    interface RecentProjectListener {
        fun clicked(project: ProjectRef)
    }
}
