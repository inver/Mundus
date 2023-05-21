package com.mbrlabs.mundus.editor.ui.modules.toolbar

import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneAddedEvent
import org.slf4j.LoggerFactory

class SceneMenu : MenuItem("Scene") {
    private val menuPopup = PopupMenu()

    companion object {
        private val log = LoggerFactory.getLogger(SceneMenu::class.java)
    }

    private val sceneItems = Array<MenuItem>()
    private val addScene = MenuItem("Add scene")


//    private val projectManager: ProjectManager = Mundus.inject()

    init {
        subMenu = menuPopup

        menuPopup.addItem(addScene)
//        eventBus.register(this)

//        addScene.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                Dialogs.showInputDialog(UI, "Add Scene", "Name:", object : InputDialogAdapter() {
//                    override fun finished(input: String?) {
//                        TODO()
////                        val project = projectManager.current
////                        val scene = projectManager.createScene(project, input)
////                        projectManager.changeScene(project, scene.name)
////                        eventBus.post(SceneAddedEvent(scene))
//                    }
//                })
//            }
//        })
//        addItem(addScene)
//
//        addSeparator()
//        buildSceneUi()
    }

    private fun buildSceneUi() {
        // remove old items
//        for (item in sceneItems) {
//            removeActor(item)
//        }
        // add new items
//        for (scene in projectManager.current.scenes) {
//            buildMenuItem(scene)
//        }

    }

    private fun buildMenuItem(sceneName: String): MenuItem? {
//        val menuItem = MenuItem(sceneName)
//        menuItem.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
////                projectManager.changeScene(projectManager.current, sceneName)
//            }
//        })
//        addItem(menuItem)
//        sceneItems.add(menuItem)
//
//        return menuItem
        return null
    }

    fun onProjectChanged(event: ProjectChangedEvent) {
//        buildSceneUi()
    }

    fun onSceneAdded(event: SceneAddedEvent) {
//        val sceneName = event.scene!!.name
//        buildMenuItem(sceneName)
//        log.trace("SceneMenu | New scene [{}] added.", sceneName)
    }
}