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

package com.mbrlabs.mundus.editor.ui.modules.inspector

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.config.UiComponentHolder
import com.mbrlabs.mundus.editor.ui.FormStyle.FormFieldStyle
import com.mbrlabs.mundus.editor.ui.widgets.CollapseWidget
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon


/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
abstract class BaseInspectorWidget(private val uiComponentHolder: UiComponentHolder, title: String) : VisTable() {

    var title: String? = null
        set(title) {
            field = title
            titleLabel.setText(title)
        }

    private val collapseBtn = uiComponentHolder.buttonFactory.createButton(SymbolIcon.EXPAND_MORE)
    private val deleteBtn = uiComponentHolder.buttonFactory.createButton(SymbolIcon.CLOSE)
    private var deletableBtnCell: Cell<*>? = null

    protected val collapsibleContent = VisTable()
    private val collapsibleWidget = CollapseWidget(collapsibleContent)
    private val titleLabel = VisLabel()

    private var deletable: Boolean = false

    init {
//        debugAll()
        collapseBtn.label.setFontScale(0.7f)
        deleteBtn.label.setFontScale(0.7f)
        deleteBtn.style.up = null

        deletable = false

        setupUI()
        setupListeners()

        this.title = title
    }

    private fun setupListeners() {
        // collapse
        collapseBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                collapse(!isCollapsed)
            }
        })

        // delete
        deleteBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onDelete()
            }
        })
    }

    private fun setupUI() {
        // header
        val header = VisTable()
        deletableBtnCell = header.add(deleteBtn).top().left().padBottom(4f)
        header.add(titleLabel)
        header.add(collapseBtn).right().top().width(20f).height(20f).expand().row()

        // add separator
        header.add(Separator(uiComponentHolder.separatorStyle)).fillX().expandX().colspan(3).row()

        // add everything to root
        add(header).expand().fill().row()

        val style = VisUI.getSkin().get(BaseWidgetInspectorStyle::class.java);
//        collapsibleContent.background(style.background)
        collapsibleContent.padTop(style.padTop)
        add(collapsibleWidget).expand().padBottom(10f).fill().row()
        isDeletable = deletable
    }

    var isDeletable: Boolean
        get() = deletable
        set(deletable) {
            this.deletable = deletable
            if (deletable) {
                deleteBtn.isVisible = true
                deletableBtnCell!!.width(20f).height(20f).padRight(5f)
            } else {
                deleteBtn.isVisible = false
                deletableBtnCell!!.width(0f).height(0f).padRight(0f)
            }
        }

    val isCollapsed: Boolean
        get() = collapsibleWidget.isCollapsed

    fun collapse(collapse: Boolean) {
        collapsibleWidget.setCollapsed(collapse, false)
        if (collapse) {
            collapseBtn.setText(SymbolIcon.EXPAND_LESS.symbol)
        } else {
            collapseBtn.setText(SymbolIcon.EXPAND_MORE.symbol)
        }
    }

    open fun onDelete() {
        //do nothing
    }

    open fun setValues(entityId: Int) {
        //do nothing
    }

    class BaseWidgetInspectorStyle {
        var background: Drawable? = null
        var padTop = 0f

        constructor()
        constructor(background: Drawable?) {
            this.background = background
        }

        constructor(style: BaseWidgetInspectorStyle) {
            background = style.background
        }
    }

    protected fun addFormField(container: Table, label: String, actor: Actor, expand: Boolean) {
        val style = VisUI.getSkin().get(FormFieldStyle::class.java)
        container.add(VisLabel(label)).top().left().padBottom(style.padBottom).padRight(style.padRight)
        val cell = container.add(actor).left().top().padBottom(style.padBottom)
        if (expand) {
            cell.expandX().fillX()
        }
        cell.row()
    }

    protected fun addFormField(container: Table, label: String, actor: Actor) {
        addFormField(container, label, actor, true)
    }
}
