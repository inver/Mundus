package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu

class EnvironmentMenu : MenuItem("Environment") {
    private val menuPopup = PopupMenu()

    val skybox = MenuItem("Skybox");
    val iblImage = MenuItem("IBL Image");

    init {
        subMenu = menuPopup

        menuPopup.addItem(skybox)
        menuPopup.addItem(iblImage)
    }
}