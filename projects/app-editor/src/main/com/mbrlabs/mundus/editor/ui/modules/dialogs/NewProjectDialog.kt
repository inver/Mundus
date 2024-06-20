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
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.UiConstants
import com.mbrlabs.mundus.editor.ui.widgets.chooser.file.FileChooserField
import com.mbrlabs.mundus.editor.ui.widgets.chooser.file.FileChooserFieldPresenter
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
@Component
class NewProjectDialog(
    private val projectManager: ProjectManager,
    private val loadingProjectDialog: LoadingProjectDialog,
    private val fileChooserFieldPresenter: FileChooserFieldPresenter
) : BaseDialog("Create New Project") {

    private val projectName = VisTextField()
    private val createBtn = VisTextButton("Create project")
    private val locationPath =
        FileChooserField(300)

    init {
        isModal = true

        fileChooserFieldPresenter.initFileChooserField(locationPath)
        locationPath.setFileMode(FileChooser.SelectionMode.DIRECTORIES)

        contentTable.add(VisLabel("Project Name")).left().row()
        contentTable.add(this.projectName).padBottom(UiConstants.PAD_BOTTOM).width(300f).fillX().row()
        contentTable.add(VisLabel("Location")).left().row()
        contentTable.add(locationPath).padBottom(UiConstants.PAD_BOTTOM_X2).growX().row()
        contentTable.add(createBtn).growX().row()

        setupListeners()

    }

    private fun setupListeners() {

        createBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val name = projectName.text
                var path = locationPath.path
                if (validateInput(name, path)) {
                    if (!path.endsWith("/")) {
                        path += "/"
                    }
                    val projectContext = projectManager.createProject(path)
                    close()
                    loadingProjectDialog.loadProjectAsync(projectContext)
                }

            }
        })

    }

    private fun validateInput(name: String?, path: String?): Boolean {
        return name != null && name.isNotEmpty() && path != null && path.isNotEmpty()
    }

}
