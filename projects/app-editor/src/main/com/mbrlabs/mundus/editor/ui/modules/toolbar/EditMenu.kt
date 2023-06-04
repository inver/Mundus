package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.badlogic.gdx.Input
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu

class EditMenu : MenuItem("Edit") {
    private val menuPopup = PopupMenu()

    private val copy = MenuItem("Copy")
    private val paste = MenuItem("Paste")
    val undo = MenuItem("Undo")
    val redo = MenuItem("Redo")

    init {
        subMenu = menuPopup

        menuPopup.addItem(copy)
        menuPopup.addItem(paste)
        menuPopup.addItem(undo)
        menuPopup.addItem(redo)

        copy.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.C)
        paste.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.P)
        undo.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.Z)
        redo.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.Y)
    }
}