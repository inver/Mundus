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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.core.ecs.component.NameComponent
import com.mbrlabs.mundus.editor.core.project.EditorCtx

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
class IdentifierWidget(private val ctx: EditorCtx) : VisTable() {

    private val active = VisCheckBox("", true)
    private val name = VisTextField("Name")
    private val tag = VisTextField("Untagged")

    init {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        add(VisLabel("Name ")).padRight(4f).left().top()
        add(name).padBottom(4f).left().top().expandX().fillX().row()
        add(VisLabel("Active ")).padRight(4f).left().top()
        add(active).padBottom(4f).left().top().row()
        add(VisLabel("Tag ")).padRight(4f).left().top()
        add(tag).top().left().expandX().fillX().row()
    }

    private fun setupListeners() {
        active.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (ctx.selectedEntity == null) {
                    return
                }

//                ctx.selectedEntityId.isActive = active.isChecked
            }
        })

        name.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (ctx.selectedEntity == null) {
                    return
                }
                ctx.getComponentByEntityId(ctx.selectedEntityId, NameComponent::class.java).name = name.text
            }
        })

    }

    fun setValues(isActive: Boolean, name: String) {
        active.isChecked = isActive
        this.name.text = name
    }

}
