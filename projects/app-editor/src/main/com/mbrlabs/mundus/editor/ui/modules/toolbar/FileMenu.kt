package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.badlogic.gdx.Input
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu

class FileMenu : MenuItem("File") {
    private val menuPopup = PopupMenu()

    val newProject = MenuItem("New Project")
    val importProject = MenuItem("Import Project")
    val exit = MenuItem("Exit")

    private val recentProjects = MenuItem("Recent Projects")
    val recentProjectsPopup = PopupMenu()
    val saveProject = MenuItem("Save Project")

    init {
        subMenu = menuPopup

        recentProjects.subMenu = recentProjectsPopup
        menuPopup.addItem(newProject)
        menuPopup.addItem(importProject)
        menuPopup.addItem(saveProject)
        menuPopup.addItem(recentProjects)
        menuPopup.addSeparator()
        menuPopup.addItem(exit)

        newProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.N)
        importProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O)
        saveProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S)
    }
}