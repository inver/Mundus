package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu

class EnvironmentMenu : MenuItem("Environment") {
    private val menuPopup = PopupMenu()

    val ambientLight = MenuItem("Ambient Light");
    val skybox = MenuItem("Skybox");
    val iblImage = MenuItem("IBL Image");
    val fog = MenuItem("Fog");

    init {
        subMenu = menuPopup

        menuPopup.addItem(ambientLight)
        menuPopup.addItem(skybox)
        menuPopup.addItem(iblImage)
        menuPopup.addItem(fog)
    }
}