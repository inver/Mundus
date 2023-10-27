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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.importer

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.mbrlabs.mundus.commons.assets.exceptions.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.chooser.image.ImageChooserField
import com.mbrlabs.mundus.editor.utils.Toaster
import com.mbrlabs.mundus.editor.utils.isImage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
@Component
class ImportTextureDialog(
    private val assetManager: EditorAssetManager,
    private val appUi: AppUi,
    private val fileChooser: FileChooser,
    private val toaster: Toaster,
    private val eventBus: EventBus
) : BaseDialog("Import Texture"), Disposable {

    companion object {
        private val log = LoggerFactory.getLogger(ImportTextureDialog::class.java)
    }

    private val importTextureTable: ImportTextureTable

    init {
        isModal = true
        isMovable = true

        val root = VisTable()
        add<Table>(root).expand().fill()
        importTextureTable = ImportTextureTable()

        root.add(importTextureTable).minWidth(300f).expand().fill().left().top()
    }

    override fun dispose() {
        importTextureTable.dispose()
    }

    override fun close() {
        super.close()
        importTextureTable.removeTexture()
    }

    /**

     */
    private inner class ImportTextureTable : VisTable(), Disposable {
        // UI elements
        private val importBtn = VisTextButton("IMPORT")
        private val imageChooserField =
            ImageChooserField(300)

        init {
            this.setupUI()
            this.setupListener()

            align(Align.topLeft)
        }

        private fun setupUI() {
            padTop(6f).padRight(6f).padBottom(22f)
            add(imageChooserField).grow().row()
            add(importBtn).grow().row()
        }

        private fun setupListener() {
            importBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    try {
                        val texture = imageChooserField.file
                        if (texture != null && texture.exists() && isImage(texture)) {
                            val asset = assetManager.createTextureAsset(texture)
                            eventBus.post(AssetImportEvent(asset))
                            close()
                            toaster.success("Texture imported")
                        } else {
                            toaster.error("There is nothing to import")
                        }
                    } catch (e: IOException) {
                        log.error("ERROR", e)
                        toaster.error("IO error")
                    } catch (ee: AssetAlreadyExistsException) {
                        log.error("ERROR", ee)
                        toaster.error("Error: There already exists a texture with the same name")
                    }

                }
            })
        }

        fun removeTexture() {
            imageChooserField.removeImage()
        }

        override fun dispose() {

        }
    }

}
