package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu

class AssetsMenu : MenuItem("Assets") {
    private val menuPopup = PopupMenu()

    val importMesh = MenuItem("Import Mesh")

    init {
        subMenu = menuPopup

        menuPopup.addItem(importMesh)
    }
}