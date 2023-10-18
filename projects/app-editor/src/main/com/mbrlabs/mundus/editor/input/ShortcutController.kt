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

package com.mbrlabs.mundus.editor.input

import com.badlogic.gdx.Input
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.modules.dialogs.ExportDialog
import com.mbrlabs.mundus.editor.ui.modules.toolbar.AppToolbar
import com.mbrlabs.mundus.editor.utils.Toaster
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
@Component
class ShortcutController(
    registry: Registry,
    private val projectManager: ProjectManager,
    private val history: CommandHistory,
    private val toolManager: ToolManager,
    private val inputService: InputService,
    private val exportDialog: ExportDialog,
    private val toaster: Toaster,
    private val toolbar: AppToolbar
) : KeyboardLayoutInputAdapter(registry) {

    private var isCtrlPressed = false

    override fun keyDown(code: Int): Boolean {
        val keycode = convertKeycode(code)

        // export
        if (keycode == Input.Keys.F1) {
            exportDialog.export()
            return true
        }

        // CTR + xyz shortcuts

        if (keycode == Input.Keys.CONTROL_LEFT) {
            isCtrlPressed = true
        }
        if (!isCtrlPressed) {
            return false
        }

        when (keycode) {
            Input.Keys.Z -> {
                history.goBack()
                return true
            }

            Input.Keys.Y -> {
                history.goForward()
                return true
            }

            Input.Keys.S -> {
                projectManager.saveCurrentProject()
                toaster.success("Project saved")
                return true
            }

            Input.Keys.T -> {
                inputService.activateTool(toolManager.translateTool)
                toolbar.updateActiveToolButton()
            }

            Input.Keys.R -> {
                inputService.activateTool(toolManager.rotateTool)
                toolbar.updateActiveToolButton()
            }

            Input.Keys.G -> {
                inputService.activateTool(toolManager.scaleTool)
                toolbar.updateActiveToolButton()
            }

            Input.Keys.F -> {
                inputService.activateTool(toolManager.selectionTool)
                toolbar.updateActiveToolButton()
            }

            Input.Keys.ESCAPE -> {
                inputService.activateDefaultTool()
            }
        }

        return false
    }

    override fun keyUp(code: Int): Boolean {
        val keycode = convertKeycode(code)
        if (keycode == Input.Keys.CONTROL_LEFT) {
            isCtrlPressed = false
        }
        return false
    }

}
