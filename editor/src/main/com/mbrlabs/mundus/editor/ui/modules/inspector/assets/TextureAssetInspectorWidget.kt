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

package com.mbrlabs.mundus.editor.ui.modules.inspector.assets

import com.badlogic.gdx.Files
import com.kotcrab.vis.ui.widget.Separator.SeparatorStyle
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset
import com.mbrlabs.mundus.editor.ui.UiConstants.PREVIEW_SIZE
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.math.roundToLong

/**
 * @author Marcus Brummer
 * @version 15-10-2016
 */
class TextureAssetInspectorWidget(separatorStyle: SeparatorStyle?) :
    BaseInspectorWidget(separatorStyle, "Texture Asset") {

    private val name = VisLabel()
    private val width = VisLabel()
    private val height = VisLabel()
    private val fileSize = VisLabel()
    private val previewImage = VisImage()

    private var textureAsset: TextureAsset? = null

    init {
        collapsibleContent.add(previewImage).height(PREVIEW_SIZE).width(PREVIEW_SIZE).row()
        collapsibleContent.add(name).growX().row()
        collapsibleContent.add(width).growX().row()
        collapsibleContent.add(height).growX().row()
        collapsibleContent.add(fileSize).growX().row()
    }

    fun setTextureAsset(texture: TextureAsset) {
        this.textureAsset = texture
        updateUI()
    }

    private fun updateUI() {
        previewImage.setDrawable(textureAsset?.texture)
        name.setText("Name: " + textureAsset?.name)
        width.setText("Width: " + textureAsset?.texture?.width + " px")
        height.setText("Height: " + textureAsset?.texture?.height + " px")

        val f = textureAsset?.meta?.file?.child(textureAsset?.meta?.additional?.file)
        if (f == null) {
            return
        }

        val type = f.type()
        var size = 0f
        if (type != Files.FileType.Classpath) {
            size = FileUtils.sizeOf(f.file()) / 1000000f
        } else {
            try {
                val uri = javaClass.classLoader.getResource(f.path())?.toURI()
                val bytes = uri?.let { File(it).length() }
                if (bytes != null) {
                    size = bytes / 1000f
                }
            } catch (e: Exception) {
                //todo write logs about exception
            }
        }
        val value = size.roundToLong().toString().reversed().chunked(3).joinToString(" ").reversed()
        fileSize.setText("Size: $value Kb")
    }

    override fun onDelete() {
        // nope
    }

    override fun setValues(entityId: Int) {
        // nope
    }

}
