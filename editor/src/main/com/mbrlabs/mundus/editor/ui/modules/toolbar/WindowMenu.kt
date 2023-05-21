package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu

class WindowMenu: MenuItem("Window") {
    private val menuPopup = PopupMenu()

    val settings = MenuItem("Settings")

    init {
        subMenu = menuPopup

        menuPopup.addItem(settings)
    }
}