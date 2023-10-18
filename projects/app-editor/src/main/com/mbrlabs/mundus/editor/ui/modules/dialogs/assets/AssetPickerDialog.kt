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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.assets

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.assets.AssetFilter
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import org.springframework.stereotype.Component

/**
 * A filterable list of materials.
 *
 * The user can pick one or no asset. The list of materials can be filtered by type before
 * showing it to the user.
 *
 * @author Marcus Brummer
 * @version 02-10-2016
 */
@Component
class AssetPickerDialog(
    eventBus: EventBus,
    private val ctx: EditorCtx,
    private val assetManager: EditorAssetManager,
    private val appUi: AppUi
) : BaseDialog("Select an asset"),
    AssetImportEvent.AssetImportListener,
    ProjectChangedEvent.ProjectChangedListener {

    private val root = VisTable()
    private val listAdapter = SimpleListAdapter(Array<Asset<*>>())
    private val list = ListView(listAdapter)
    private val noneBtn = VisTextButton("None / Remove old asset")

    private var filter: AssetFilter? = null
    private var listener: AssetPickerListener? = null

    init {
        eventBus.register(this)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        root.add(list.mainTable).grow().size(350f, 450f).row()
        root.add(noneBtn).padTop(10f).grow().row()
        add(root).padRight(5f).padBottom(5f).grow().row()
    }

    private fun setupListeners() {
        list.setItemClickListener { item ->
            if (listener != null) {
                listener!!.onSelected(item)
                close()
            }
        }

        noneBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (listener != null) {
                    listener!!.onSelected(null)
                    close()
                }
            }
        })
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        reloadData()
    }

    override fun onAssetImported(event: AssetImportEvent) {
        reloadData()
    }

    private fun reloadData() {
        listAdapter.clear()

        // filter assets
        for (entry in ctx.current.projectAssets.entries) {
            if (filter != null) {
                if (filter!!.ignore(entry.value)) {
                    continue
                }
            }
            listAdapter.add(entry.value)
        }

        listAdapter.itemsDataChanged()
    }

    /**
     * Shows the dialog.
     *
     * @param showNoneAsset if true the user will be able to select a NONE asset
     * @param filter optional asset type filter
     * @listener picker listener
     */
    fun show(showNoneAsset: Boolean, filter: AssetFilter?, listener: AssetPickerListener) {
        this.listener = listener
        this.filter = filter
        if (showNoneAsset) {
            noneBtn.isDisabled = false
            noneBtn.touchable = Touchable.enabled
        } else {
            noneBtn.isDisabled = true
            noneBtn.touchable = Touchable.disabled
        }
        reloadData()
        appUi.showDialog(this)
    }
}
