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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.mbrlabs.mundus.editor.tools.Tool
import com.mbrlabs.mundus.editor.tools.ToolManager
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
@Component
class InputService(private val toolManager: ToolManager) : InputMultiplexer() {

    // TODO move this to Editor.class
    init {
        Gdx.input.inputProcessor = this
    }

    fun activateTool(tool: Tool) {
        removeProcessor(toolManager.activeTool)
        toolManager.activateTool(tool)
        //add tool to input manager after this toolManager
        var index = 0
        for (p in processors) {
            if (p.equals(toolManager)) {
                addProcessor(index, toolManager.activeTool)
                break
            } else {
                index++
            }
        }
    }

    fun deactivateTool() {
        removeProcessor(toolManager.activeTool)
        toolManager.deactivateTool()
    }

    fun activateDefaultTool() {
        // todo replace to select tool
        activateTool(toolManager.translateTool)
    }
}
