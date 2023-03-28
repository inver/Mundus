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

package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.widgets.ImageChooserField
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
@Component
class SkyboxDialog(
    private val ctx: EditorCtx,
    eventBus: EventBus,
    appUi: AppUi,
    fileChooser: FileChooser
) : BaseDialog("Skybox"),
    ProjectChangedEvent.ProjectChangedListener,
    SceneChangedEvent.SceneChangedListener {

    private val positiveX = ImageChooserField(appUi, 100, fileChooser)
    private var negativeX = ImageChooserField(appUi, 100, fileChooser)
    private var positiveY = ImageChooserField(appUi, 100, fileChooser)
    private var negativeY = ImageChooserField(appUi, 100, fileChooser)
    private var positiveZ = ImageChooserField(appUi, 100, fileChooser)
    private var negativeZ = ImageChooserField(appUi, 100, fileChooser)

    private var createBtn = VisTextButton("Create skybox")
    private var defaultBtn = VisTextButton("Create default skybox")
    private var deleteBtn = VisTextButton("Remove Skybox")

    init {
        eventBus.register(this)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        positiveX.setButtonText("positiveX")
        negativeX.setButtonText("negativeX")
        positiveY.setButtonText("positiveY")
        negativeY.setButtonText("negativeY")
        positiveZ.setButtonText("positiveZ")
        negativeZ.setButtonText("negativeZ")

        val root = VisTable()
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root).left().top()
        root.add(VisLabel("The 6 images must be square and of equal size")).colspan(3).row()
        root.addSeparator().colspan(3).row()
        root.add(positiveX)
        root.add(negativeX)
        root.add(positiveY).row()
        root.add(negativeY)
        root.add(positiveZ)
        root.add(negativeZ).row()
        root.add(createBtn).padTop(15f).padLeft(6f).padRight(6f).expandX().fillX().colspan(3).row()

        val tab = VisTable()
        tab.add(defaultBtn).expandX().padRight(3f).fillX()
        tab.add(deleteBtn).expandX().fillX().padLeft(3f).row()
        root.add(tab).fillX().expandX().padTop(5f).padLeft(6f).padRight(6f).colspan(3).row()
    }

    private fun setupListeners() {
        // create btn
        createBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                val scene = ctx.current.currentScene;
//                val oldSkybox = scene.skyboxName
//                oldSkybox?.dispose()

//                scene.skybox = Skybox(
//                    positiveX.file, negativeX.file,
//                    positiveY.file, negativeY.file, positiveZ.file, negativeZ.file
//                )
                resetImages()
            }
        })

        // default skybox btn
        defaultBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val scene = ctx.current.currentScene

//                if (scene.skyboxName != null) {
//                    scene.skyboxName.dispose()
//                }
//                scene.skybox = createDefaultSkybox()
                resetImages()
            }
        })

        // delete skybox btn
        deleteBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val scene = ctx.current.currentScene

//                scene.skyboxName.dispose()
//                scene.skybox = null
                resetImages()
            }
        })

    }

    private fun resetImages() {
//        val skybox = ctx.current.getCurrentScene().skyboxName
//        if (skybox != null) {
//            positiveX.setImage(skybox.positiveX)
//            negativeX.setImage(skybox.negativeX)
//            positiveY.setImage(skybox.positiveY)
//            negativeY.setImage(skybox.negativeY)
//            positiveZ.setImage(skybox.positiveY)
//            negativeZ.setImage(skybox.negativeZ)
//        } else {
//            positiveX.setImage(null)
//            negativeX.setImage(null)
//            positiveY.setImage(null)
//            negativeY.setImage(null)
//            positiveZ.setImage(null)
//            negativeZ.setImage(null)
//        }
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        resetImages()
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        resetImages()
    }

}
